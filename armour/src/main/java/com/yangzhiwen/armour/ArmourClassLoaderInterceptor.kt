package com.yangzhiwen.armour

import com.yangzhiwen.armour.compass.NavigatorComponent

/**
 * Created by yangzhiwen on 17/8/14.
 */
class ArmourClassLoaderInterceptor {
    companion object {
        val instance = ArmourClassLoaderInterceptor()
    }

    val componentMap = mutableMapOf<String, NavigatorComponent>()

    fun onLoadClass(name: String?): Class<*>? {
        val component = componentMap[name] ?: return null

        println("**********************************************")
        println("ArmourClassLoaderInterceptor || load class $name to component $component")
        println("**********************************************")

        return Armour.instance()?.getPlugin(component.module)?.aPluginClassloader?.loadClass(component.realComponent) ?: return null
    }

    fun addLoadInterceptor(name: String, component: NavigatorComponent) {
        componentMap[name] = component
    }
}