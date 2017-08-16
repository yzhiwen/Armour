package com.yangzhiwen.armour.compass

import android.content.Context
import com.yangzhiwen.compass.center.ComponentHandlerCenter

/**
 * Created by yangzhiwen on 2017/8/12.
 */
class Navigator {

    var context: Context? = null
    val realComponentToModule = mutableMapOf<String, String>()

    companion object {
        val instance = Navigator()
    }

    fun getModuleByRealComponent(realComponent: String): String? = realComponentToModule[realComponent]

    fun getModule(module: String): NavigatorModule? = modules[module]

    fun registerModule(module: String): NavigatorModule {
        val m = NavigatorModule(module)
        modules.put(module, m)
        return m
    }

    fun getModuleWithRegister(module: String): NavigatorModule = getModule(module) ?: registerModule(module) // !! 这里不应该发生Exception

    // module -> component list | yes
    val modules = mutableMapOf<String, NavigatorModule>()
    // component -> module | yes
    // realComponent -> component | no

    /**
     * 这样是考虑到扩展性 Component可以进行扩展，在相应Handler进行转换 & handle
     */
    fun registerComponent(component: NavigatorComponent) {
        getModuleWithRegister(component.module).registerComponent(component)
        realComponentToModule[component.realComponent] = component.module
    }

    fun registerComponentHandler(componentType: String, handler: NavigatorComponentHandler)
            = ComponentHandlerCenter.instance.registerComponentHandler(componentType, handler)

    fun registerInterceptor(interceptor: NavigatorInterceptor) {

    }

    fun nav(module: String, name: String, operation: String, jsonArg: String) {
        val cmp = modules[module]?.getComponent(name) ?: return
        val type = cmp.type
        ComponentHandlerCenter.instance.getComponentHandler(type)?.onHandle(cmp, operation, jsonArg)
    }
}