package com.yangzhiwen.compass

/**
 * Created by yangzhiwen on 2017/8/12.
 */
class NavigatorModule(module: String) {

    val moduleName = module
    val componentMap = mutableMapOf<String, NavigatorComponent>()

    fun registerComponent(component: String, realComponent: String, type: String)
    {
        println("register .. ${component}")
        componentMap.put(component, NavigatorComponent(this, component, realComponent, type))
    }

    fun getComponent(component: String) = componentMap[component]
}