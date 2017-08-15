package com.yangzhiwen.compass

/**
 * Created by yangzhiwen on 2017/8/12.
 */
class NavigatorModule(module: String) {

    val moduleName = module
    val componentMap = mutableMapOf<String, NavigatorComponent>()

    fun registerComponent(component: String, realComponent: String, type: String) {
        componentMap.put(component, NavigatorComponent(moduleName, component, realComponent, type))
    }

    fun getComponent(component: String) = componentMap[component]
}