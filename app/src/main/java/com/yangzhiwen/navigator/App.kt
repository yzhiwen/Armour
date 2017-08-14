package com.yangzhiwen.navigator

import android.app.Activity
import android.app.Application
import android.content.BroadcastReceiver
import android.content.Context
import android.content.IntentFilter
import android.content.res.Resources
import android.os.Bundle
import android.view.ContextThemeWrapper
import com.yangzhiwen.armour.Armour
import com.yangzhiwen.compass.Navigator
import com.yangzhiwen.navigator.ext.armour.ArmourClassLoaderInterceptorImpl
import com.yangzhiwen.navigator.ext.navigator.registerActivityComponent
import com.yangzhiwen.navigator.ext.navigator.registerActivityComponentHandler
import com.yangzhiwen.navigator.ext.navigator.registerReceiverComponent
import kotlin.concurrent.thread
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.lang.reflect.Field


/**
 * Created by yangzhiwen on 2017/8/13.
 */
class App : Application() {

    companion object {
        var instance: App? = null
    }

    class CallBack(path: String) : ActivityLifecycleCallbacks {
        var apkPath = path
        override fun onActivityResumed(p0: Activity?) {

        }

        override fun onActivityStarted(p0: Activity?) {
        }

        override fun onActivityDestroyed(p0: Activity?) {
        }

        override fun onActivitySaveInstanceState(p0: Activity?, p1: Bundle?) {
        }

        override fun onActivityStopped(p0: Activity?) {
        }

        override fun onActivityCreated(activity: Activity?, p1: Bundle?) {

            println("===== on activity created $activity")
            if (activity == null) return
            if (activity.javaClass.name != "com.yangzhiwen.demo.CenterActivity") return

            println("String.javaClass: ${String.javaClass}")
            println("".javaClass)

            val oldResources = activity.resources
            val newAssetManager = activity.resources.assets.javaClass.newInstance()
            val addAssetPathMethod = activity.resources.assets.javaClass.getDeclaredMethod("addAssetPath", String::class.java)
            addAssetPathMethod.isAccessible = true
            val temp = addAssetPathMethod.invoke(newAssetManager, apkPath) as Int
            if (temp == 0) {
                println("addAssetPath return 0")
            }

            val mBaseField = findField(activity.javaClass, "mBase") ?: return
            mBaseField.isAccessible = true
            val mBase = mBaseField.get(activity)
            println("mBase : $mBase")

            val resField = findField(mBase.javaClass, "mResources") ?: return
            resField.isAccessible = true
            println("resField : $resField : ${resField.get(mBase)}")

            val newR = Resources(newAssetManager, oldResources.displayMetrics, oldResources.configuration)

            resField.set(mBase, newR)


            val resAField = findField(activity.javaClass, "mResources") ?: return
            resAField.isAccessible = true
            resAField.set(activity, newR)

//            val oldResources = resField.get(activity) as Resources
//
            println("old res: $oldResources || new res: ${activity.resources}")
        }

        override fun onActivityPaused(p0: Activity?) {

        }

        fun findField(aClass: Class<*>?, name: String): Field? {
            if (aClass == null) return null
            try {
                val mResourcesField = aClass.getDeclaredField(name)
                println("find $name in ${aClass.name}")
                return mResourcesField
            } catch (e: Exception) {
//                e.printStackTrace()
                println("not find $name in ${aClass.name}")
                return findField(aClass.superclass, name)
            }
        }
    }


    override fun onCreate() {
        super.onCreate()
        instance = this


        Navigator.instance.registerActivityComponent("host", "pay", "com.yangzhiwen.navigator.MainActivity")
        Navigator.instance.registerActivityComponent("host", "other", "com.yangzhiwen.navigator.OtherActivity")
        Navigator.instance.registerActivityComponentHandler()


        Armour.instance(this)
        Armour.instance(this).classLoaderInterceptor = ArmourClassLoaderInterceptorImpl()
        thread {
            val outPath = copy()
            registerActivityLifecycleCallbacks(CallBack(outPath))


            val plugin = Armour.instance(this).instantPlugin("user_center", outPath)

            Navigator.instance.registerActivityComponent("user_center", "setting", "com.yangzhiwen.demo.MainActivity")
            Navigator.instance.registerActivityComponent("user_center", "center", "com.yangzhiwen.demo.CenterActivity")

            Armour.instance(this).classLoaderInterceptor?.classMapToModule?.put("com.yangzhiwen.navigator.ProxyActivity", "user_center")
            Armour.instance(this).classLoaderInterceptor?.realClassMap?.put("com.yangzhiwen.navigator.ProxyActivity", "com.yangzhiwen.demo.CenterActivity")


            val actions = arrayOf("user_center_msg", "user_center_setting_msg")
            Navigator.instance.registerReceiverComponent(this, "user_center", "user_center_receiver", "com.yangzhiwen.demo.UserCenterReceiver", *actions)

//            plugin?.classloader?.loadClass("com.yangzhiwen.demo.UserCenterReceiver")
            val recevier = Armour.instance()?.getPlugin("user_center")?.classloader?.loadClass("com.yangzhiwen.demo.UserCenterReceiver")?.newInstance() as BroadcastReceiver
            val filter = IntentFilter()
            for (action in actions) {
                filter.addAction(action)
            }
            registerReceiver(recevier, filter)

        }


    }

    private fun copy(): String {
        val filePath = "sdcard/main.apk"
        val outPath = filesDir.absolutePath + "/patch/out.apk"

        if (!File(outPath).exists()) {
            File(outPath).parentFile.mkdirs()
            File(outPath).createNewFile()
        }

        val fi = FileInputStream(File(filePath))
        val fo = FileOutputStream(File(outPath), false)

        val buffer = ByteArray(4096)
        while (true) {
            val len = fi.read(buffer)
            if (len <= 0) break
            fo.write(buffer, 0, len)
        }

        fo.flush()
        fo.close()
        fi.close()
        return outPath
    }
}