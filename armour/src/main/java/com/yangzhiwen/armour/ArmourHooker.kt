package com.yangzhiwen.armour

import android.app.Activity
import android.content.Context
import android.content.Intent
import com.yangzhiwen.armour.compass.Navigator
import com.yangzhiwen.armour.ext.compass.ActivityComponent
import com.yangzhiwen.armour.ext.compass.ServiceComponent
import com.yangzhiwen.armour.proxy.ArmourActivity
import com.yangzhiwen.armour.proxy.ArmourRemoteService
import com.yangzhiwen.armour.proxy.ArmourService

/**
 * Created by yangzhiwen on 17/8/18.
 */
class ArmourHooker {
    fun execStartActivity(who: Context, intent: Intent): Intent? {
        println("ArmourInstrumentation execStartActivity :: ${intent.component.className}")
        // todo component null
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

    fun newActivity(cl: ClassLoader?, className: String?, intent: Intent?): Activity? {
        println("ArmourInstrumentation newActivity :: $className")
        if (intent == null) return null
        val ra = intent.getParcelableExtra<Intent>("real")
        if (ra != null) {
            val componentName = ra.component?.className ?: return null
            val module = Navigator.instance.getModuleByRealComponent(componentName) ?: return null
            val aPlugin = Armour.instance()?.getPlugin(module) ?: return null
            val realActivity = aPlugin.aPluginClassloader.loadClass(componentName).newInstance() as Activity
            println("ArmourInstrumentation real activity :: $realActivity")
            realActivity.intent = ra
            return realActivity
        }
        return null
    }

    fun callActivityOnCreate(activity: Activity) {
        println("callActivityOnCreate == $activity")
//        println("callActivityOnCreate == ${activity.intent}") // todo 获取的是代理的ComponentName
//        activity.componentName // todo 获取的是代理的ComponentName
        val componentName = activity.javaClass.name
        println("callActivityOnCreate == $componentName")
        val module = Navigator.instance.getModuleByRealComponent(componentName) ?: return
        val aPlugin = Armour.instance()?.getPlugin(module) ?: return
        println("callActivityOnCreate hook resources")
        // hook ContextThemeWrapper 的 mResource
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