package com.yangzhiwen.armour

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Application
import android.app.Instrumentation
import android.content.Context
import android.content.Intent
import android.net.Uri
import com.yangzhiwen.armour.compass.Navigator
import com.yangzhiwen.armour.ext.Hacker
import com.yangzhiwen.armour.ext.compass.ActivityComponent
import com.yangzhiwen.armour.ext.compass.ProviderComponent
import com.yangzhiwen.armour.ext.compass.ServiceComponent
import com.yangzhiwen.armour.ext.wrapUrl
import com.yangzhiwen.armour.proxy.ArmourActivity
import com.yangzhiwen.armour.proxy.ArmourContentProvider
import com.yangzhiwen.armour.proxy.ArmourRemoteService
import com.yangzhiwen.armour.proxy.ArmourService
import java.lang.reflect.Proxy

/**
 * Created by yangzhiwen on 2017/8/13.
 */
class ArmourHacker(val application: Application) {

    val activityThread: Any by lazy {
        Hacker.on(application.baseContext.javaClass)
                .field("mMainThread")!!.get(application.baseContext)
    }


    companion object {

        @SuppressLint("StaticFieldLeak")
        private var instance: ArmourHacker? = null

        fun instance(application: Application): ArmourHacker {
            if (instance == null) instance = ArmourHacker(application)
            return instance!!
        }
    }

    fun hackClassLoader(context: Application, classLoader: ArmourClassLoader) {
        val packageInfo = Hacker.on(context.baseContext.javaClass)
                .field("mPackageInfo")!!.get(context.baseContext)

        Hacker.on(packageInfo.javaClass)
                .field("mClassLoader")!!.set(packageInfo, classLoader)
    }

    fun hackInstrumentation(armour: Armour, application: Application): ArmourInstrumentation {
        println("hackInstrumentation start")
        val ins = Hacker.on(activityThread.javaClass).field("mInstrumentation")!!

        val base = ins.get(activityThread) as Instrumentation
        val armourInstrumentation = ArmourInstrumentation(armour, base)
        ins.set(activityThread, armourInstrumentation)
        return armourInstrumentation
    }


    fun hackContentProvider(application: Application): Any? {
        val base = application.contentResolver
        val icp = Hacker.on(base.javaClass)
                .declaredMethod("acquireProvider", Context::class.java, String::class.java)!!.invoke(base, application, ArmourContentProvider.AUTHORITY)

        val cl = arrayOf(Class.forName("android.content.IContentProvider"))
        val proxy = Proxy.newProxyInstance(application.classLoader, cl, ArmourIContentProvider(icp, this))

        return proxy
    }

    fun onIContentProviderInvoke(params: Array<Any>?) {
        if (params == null) return

        val set = Navigator.instance.readComponentToComponent.values.filter {
            it is ProviderComponent && it.isPlugin
        }
        println(" || $set")

        var index = -1
        for (obj in params) {
            index++
            if (obj is Uri) {
                val result = set.filter {
                    it as ProviderComponent
                    it.url == obj
                }
                println(" || $result")
                if (!result.isEmpty()) {
                    val component = result.first()
                    params[index] = wrapUrl(component, obj)
                    break
                }
            }
        }
    }


    fun execStartActivity(who: Context, intent: Intent): Intent? {
        println("ArmourInstrumentation execStartActivity :: ${intent.component.className}")
        val componentName = intent.component.className
        val component = Navigator.instance.getComponentByRealComponent(componentName)
        println("ArmourInstrumentation execStartActivity :: module $component")
        if (component is ActivityComponent && component.isPlugin) {
            // todo 匹配占坑 Activity
            val proxy = Intent(who, ArmourActivity::class.java)
            proxy.putExtra("real", intent)
            return proxy
        }
        return intent
    }

    fun newActivity(className: String?, intent: Intent?): Activity? {
        println("ArmourInstrumentation newActivity :: $className")
        if (intent == null) return null
        val ra = intent.getParcelableExtra<Intent>("real")
        if (ra != null) {
            val componentName = ra.component?.className ?: return null
            val module = Navigator.instance.getModuleByRealComponent(componentName) ?: return null
            val aPlugin = Armour.instance()?.getPlugin(module) ?: return null
            val realActivity = aPlugin.aPluginClassloader.loadClass(componentName).newInstance() as Activity
            println("ArmourInstrumentation real activity :: $realActivity")
            realActivity.intent = ra // todo
            return realActivity
        }
        return null
    }

    fun callActivityOnCreate(activity: Activity) {
//        println("callActivityOnCreate == ${activity.intent}") // todo 获取的是代理的ComponentName
//        activity.componentName // todo 获取的是代理的ComponentName
        val componentName = activity.javaClass.name
        println("callActivityOnCreate || activity $activity || component name $componentName")
        val module = Navigator.instance.getModuleByRealComponent(componentName) ?: return
        val aPlugin = Armour.instance()?.getPlugin(module) ?: return
//        hook ContextThemeWrapper 的 mResource
        Hacker.on(activity.javaClass)
                .field("mResources")
                ?.set(activity, aPlugin.aPluginResources)

        // hook ContextWrapper mBase
        val mBase = Hacker.on(activity.javaClass)
                .field("mBase")
                ?.get(activity) as Context

        val aPluginContext = aPlugin.getAPluginContext(componentName, mBase)
        Hacker.on(activity.javaClass)
                .field("mBase")
                ?.set(activity, aPluginContext)
    }

    fun onServiceHook(context: Context, componentName: String, operation: String, connOperation: () -> Unit): Boolean {
        val component = Navigator.instance.getComponentByRealComponent(componentName) as? ServiceComponent ?: return false
        if (!component.isPlugin) return false

        val intent: Intent
        if (component.isRemote) intent = Intent(context, ArmourRemoteService::class.java)
        else intent = Intent(context, ArmourService::class.java)

        connOperation()
        intent.putExtra(ArmourService.ARG_OP, operation)
        intent.putExtra(ArmourService.MODULE_NAME, component.module)
        intent.putExtra(ArmourService.COMPONENT, component.realComponent)

        context.startService(intent)
        return true
    }
}