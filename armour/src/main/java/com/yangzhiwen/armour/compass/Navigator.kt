package com.yangzhiwen.armour.compass


/**
 * Created by yangzhiwen on 2017/8/12.
 */
class Navigator {

    // module -> component list | yes
    // component -> module | yes
    // realComponent -> component | no
    val modules = mutableMapOf<String, NavigatorModule>()
    val realComponentToModule = mutableMapOf<String, String>()
    val readComponentToComponent = mutableMapOf<String, NavigatorComponent>()

    companion object {
        val instance = Navigator()
    }

    fun getModuleByRealComponent(realComponent: String): String? = realComponentToModule[realComponent]

    fun getComponentByRealComponent(realComponent: String): NavigatorComponent? = readComponentToComponent[realComponent]

    fun getModule(module: String): NavigatorModule? = modules[module]

    fun register(module: String, isPlugin: Boolean, init: NavigatorModule.() -> Unit) {
        var nm = modules[module]
        if (nm == null) {
            nm = NavigatorModule(module, isPlugin)
            modules[module] = nm
        }
        nm.init()
    }
}