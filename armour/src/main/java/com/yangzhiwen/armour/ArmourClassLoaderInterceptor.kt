package com.yangzhiwen.armour

/**
 * Created by yangzhiwen on 17/8/14.
 */
abstract class ArmourClassLoaderInterceptor {

    val classMapToModule = mutableMapOf<String, String>()
    val realClassMap = mutableMapOf<String, String>()

    open fun onLoadClass(name: String?): Class<*>? {

        val module = classMapToModule[name] ?: return null
        val realClass = realClassMap[name] ?: return null

        println("**********************************************")
        println("ArmourClassLoaderInterceptor || load class $name to module $module for real class $realClass")
        println("**********************************************")

        return Armour.instance()?.getPlugin(module)?.classloader?.loadClass(realClass) ?: return null
    }
}