package com.yangzhiwen.navigator

import android.app.Activity
import android.app.Instrumentation
import android.content.Intent
import android.content.res.Resources
import android.view.ContextThemeWrapper
import com.yangzhiwen.armour.Armour

/**
 * Created by yangzhiwen on 17/8/15.
 */
class ArmourInstrumentation(baseArg: Instrumentation, armourArg: Armour) : Instrumentation() {

    val base = baseArg
    val armour = armourArg


    override fun newActivity(cl: ClassLoader?, className: String?, intent: Intent?): Activity {
        try {
            println("load activity $className")
            if(className == "com.yangzhiwen.navigator.ProxyActivity") throw IllegalArgumentException("ddd")
            cl?.loadClass(className)
        } catch (e: Exception) {
            val curClassLoader = armour.getPlugin("user_center")?.classloader
            if (curClassLoader != null) {
                println("...........dazz")
                val activity = base.newActivity(curClassLoader, "com.yangzhiwen.demo.CenterActivity", intent)
                val oldResources = App.res ?: return super.newActivity(cl, className, intent)
                val newAssetManager = oldResources.assets.javaClass.newInstance()
                val addAssetPathMethod = oldResources.assets.javaClass.getDeclaredMethod("addAssetPath", String::class.java)
                addAssetPathMethod.isAccessible = true
                val temp = addAssetPathMethod.invoke(newAssetManager, App.path) as Int
                if (temp == 0) {
                    println("addAssetPath return 0")
                }

                val newR = Resources(newAssetManager, oldResources.displayMetrics, oldResources.configuration)

                try {
                    // for 4.1+
                    ReflectUtil.setField(ContextThemeWrapper::class.java, activity, "mResources", newR)
                } catch (ignored: Exception) {
                    ignored.printStackTrace()
                    // ignored.
                }
                return activity
            }
        }

        return super.newActivity(cl, className, intent)

    }
}