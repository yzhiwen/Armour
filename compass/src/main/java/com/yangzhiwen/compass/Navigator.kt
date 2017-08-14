package com.yangzhiwen.compass

import android.content.Context
import com.yangzhiwen.compass.center.ComponentHandlerCenter

/**
 * Created by yangzhiwen on 2017/8/12.
 */
class Navigator {

    var context: Context? = null
    val modules = mutableMapOf<String, NavigatorModule>()

    companion object {
        val instance = Navigator()
    }

    fun getModule(module: String): NavigatorModule? = modules[module]

    fun registerModule(module: String): NavigatorModule? {
        if (getModule(module) == null) modules.put(module, NavigatorModule(module))
        return getModule(module)
    }

    fun registerComponent(module: String, component: String, componentType: String, realComponent: String) {
        if (getModule(module) == null) registerModule(module)
        getModule(module)?.registerComponent(component, realComponent, componentType)
    }

    fun registerComponentHandler(componentType: String, handler: NavigatorComponentHandler)
            = ComponentHandlerCenter.instance.registerComponentHandler(componentType, handler)

    fun registerInterceptor(interceptor: NavigatorInterceptor) {

    }

    fun nav(module: String, component: String, jsonArg: String) {
        val cmp = modules[module]?.getComponent(component) ?: return
        val type = cmp.type
        ComponentHandlerCenter.instance.getComponentHandler(type)?.onHandle(cmp, jsonArg)
    }
}