package com.yangzhiwen.compass.center

import com.yangzhiwen.compass.NavigatorComponentHandler

/**
 * Created by yangzhiwen on 2017/8/12.
 */
class ComponentHandlerCenter {

    companion object {
        val instance = ComponentHandlerCenter()
    }

    val map = mutableMapOf<String, NavigatorComponentHandler>()

    fun registerComponentHandler(componentType: String, handler: NavigatorComponentHandler) {
        map[componentType] = handler
    }

    fun getComponentHandler(componentType: String) = map[componentType]
}