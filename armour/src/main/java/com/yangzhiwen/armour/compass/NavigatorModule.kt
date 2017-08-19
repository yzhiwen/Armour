package com.yangzhiwen.armour.compass

/**
 * Created by yangzhiwen on 2017/8/12.
 */
class NavigatorModule(val moduleName: String, val isPlugin: Boolean) {

    val componentMap = mutableMapOf<String, NavigatorComponent>()

    fun registerComponent(component: NavigatorComponent) {
        componentMap.put(component.name, component)

        Navigator.instance.realComponentToModule[component.realComponent] = component.module
        Navigator.instance.readComponentToComponent[component.realComponent] = component
    }

    fun getComponent(component: String) = componentMap[component]
}