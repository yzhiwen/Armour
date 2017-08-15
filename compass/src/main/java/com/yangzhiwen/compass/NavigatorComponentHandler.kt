package com.yangzhiwen.compass

/**
 * Component Handler : 路由Activity Handler、路由网络请求 Handler
 *
 * Created by yangzhiwen on 2017/8/12.
 */
abstract class NavigatorComponentHandler(componentType: String) {
    abstract fun onHandle(component: NavigatorComponent, operation: String, jsonArg: String)
}