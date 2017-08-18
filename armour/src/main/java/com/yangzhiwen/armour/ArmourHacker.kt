package com.yangzhiwen.armour

import android.app.Activity
import android.app.Application
import android.app.Instrumentation
import android.content.res.Resources
import com.yangzhiwen.armour.compass.Navigator

/**
 * Created by yangzhiwen on 2017/8/13.
 */
class ArmourHacker {

    companion object {
        val instance = ArmourHacker()
    }

    fun hackClassLoader(context: Application, classLoader: ArmourClassLoader) {
        val packageInfo = Hacker.on(context.baseContext.javaClass)
                .field("mPackageInfo")
                ?.get(context.baseContext) ?: return

        Hacker.on(packageInfo.javaClass)
                .field("mClassLoader")
                ?.set(packageInfo, classLoader)
    }

    fun hackInstrumentation(armour: Armour, application: Application): ArmourInstrumentation? {
        println("hackInstrumentation start")
        val activityThread = Hacker.on(application.baseContext.javaClass)
                .field("mMainThread")
                ?.get(application.baseContext)
        if (activityThread == null) {
            println("hackInstrumentation error")
            return null
        }

        val ins = Hacker.on(activityThread.javaClass)
                .field("mInstrumentation")
        if (ins == null) {
            println("hackInstrumentation error")
            return null
        }
        val base = ins.get(activityThread) as Instrumentation
        val armourInstrumentation = ArmourInstrumentation(armour, base)
        ins.set(activityThread, armourInstrumentation)
        return armourInstrumentation
    }

    @Deprecated("delete")
    fun hookActivityResource(activity: Activity) {
        // 根据 module 是否空 判断是否插件进行hook
        val module = Navigator.instance.getModuleByRealComponent(activity.javaClass.name) ?: return
        val apkPath = Armour.instance()?.getPlugin(module)?.pluginPath ?: return

        val oldResources = activity.resources ?: return //todo 可能为空
        val newAssetManager = oldResources.assets.javaClass.newInstance()
        val result = Hacker.on(newAssetManager.javaClass)
                .method("addAssetPath", String::class.java)
                ?.invoke(newAssetManager, apkPath) as Int
        if (result == 0) {
            println("addAssetPath return 0 on the module: $module")
            return
        }

        // hook ContextThemeWrapper 的 mResource
        val newR = Resources(newAssetManager, oldResources.displayMetrics, oldResources.configuration)
        Hacker.on(activity.javaClass)
                .field("mResources")
                ?.set(activity, newR)


        // hook mBase 的 mResource属性
//        val mBaseField = findField(activity.javaClass, "mBase") ?: return
//        mBaseField.isAccessible = true
//        val mBase = mBaseField.get(activity)
//        println("mBase : $mBase")
//
//        val resField = findField(mBase.javaClass, "mResources") ?: return
//        resField.isAccessible = true
//        println("resField : $resField : ${resField.get(mBase)}")

//        resField.set(mBase, newR)
    }
}