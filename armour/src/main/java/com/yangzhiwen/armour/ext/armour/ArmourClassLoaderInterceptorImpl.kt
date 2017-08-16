package com.yangzhiwen.armour.ext.armour

import com.yangzhiwen.armour.Armour
import com.yangzhiwen.armour.ArmourClassLoaderInterceptor

/**
 * Created by yangzhiwen on 17/8/14.
 */
class ArmourClassLoaderInterceptorImpl : ArmourClassLoaderInterceptor() {
    companion object {
        val instance = ArmourClassLoaderInterceptorImpl()
    }


   override fun onLoadClass(name: String?): Class<*>? {

        val module = classMapToModule[name] ?: return null
        val realClass = realClassMap[name] ?: return null

        println("**********************************************")
        println("ArmourClassLoaderInterceptor || load class $name to module $module for real class $realClass")
        println("**********************************************")



        return Armour.instance()?.getPlugin(module)?.classloader?.loadClass(realClass) ?: return null
    }
}