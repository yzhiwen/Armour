package com.yangzhiwen.compass

/**
 * Created by yangzhiwen on 2017/8/12.
 */
class NavigatorModule(moduleName: String) {

    val name = moduleName
    val componentMap = mutableMapOf<String, NavigatorComponent>()

    fun registerComponent(component: NavigatorComponent) {
        componentMap.put(component.name, component)
    }

    fun getComponent(component: String) = componentMap[component]
}