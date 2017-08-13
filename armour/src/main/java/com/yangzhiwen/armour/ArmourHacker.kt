package com.yangzhiwen.armour

import android.app.Application
import android.content.Context

/**
 * Created by yangzhiwen on 2017/8/13.
 */
class ArmourHacker {

    companion object {
        val instance = ArmourHacker()
    }

    fun hackClassLoader(context: Application, classLoader: ArmourClassLoader) {
        val packageInfoField = context.baseContext.javaClass.getDeclaredField("mPackageInfo")
        packageInfoField.isAccessible = true
        val packageInfo = packageInfoField.get(context.baseContext)

        val defaultClassLoaderField = packageInfo.javaClass.getDeclaredField("mClassLoader")
        defaultClassLoaderField.isAccessible = true
        defaultClassLoaderField.set(packageInfo, classLoader)
    }
}