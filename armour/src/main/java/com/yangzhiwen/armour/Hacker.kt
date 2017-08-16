package com.yangzhiwen.armour

import android.app.Activity
import android.content.res.Resources
import com.yangzhiwen.armour.compass.Navigator
import java.lang.reflect.Field

/**
 * Created by yangzhiwen on 17/8/15.
 */
class Hacker {

    companion object {
        val instance = Hacker()
    }

    // todo 再封装 抽离
    fun hookActivityResource(activity: Activity) {
        // 是否需要hook
        // apkPath 查找activity对应插件路径
        val module = Navigator.instance.getModuleByRealComponent(activity.javaClass.name) ?: return
        val apkPath = Armour.instance()?.getPlugin(module)?.pluginPath ?: return

        val oldResources = activity.resources ?: return //todo 可能为空
        val newAssetManager = oldResources.assets.javaClass.newInstance()
        val addAssetPathMethod = newAssetManager.javaClass.getDeclaredMethod("addAssetPath", String::class.java)
        addAssetPathMethod.isAccessible = true
        val temp = addAssetPathMethod.invoke(newAssetManager, apkPath) as Int
        if (temp == 0) { // add fail
            println("addAssetPath return 0")
            return
        }

        // hook ContextThemeWrapper 的 mResource
        val newR = Resources(newAssetManager, oldResources.displayMetrics, oldResources.configuration)
        val resAField = findField(activity.javaClass, "mResources") ?: return
        resAField.isAccessible = true
        resAField.set(activity, newR)


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