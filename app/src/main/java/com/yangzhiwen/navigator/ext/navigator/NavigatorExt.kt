package com.yangzhiwen.navigator.ext.navigator

import android.content.Context
import com.yangzhiwen.compass.ComponentType
import com.yangzhiwen.compass.Navigator


/**
 * Created by yangzhiwen on 2017/8/12.
 */
fun Navigator.registerActivityComponent(module: String, component: String, realComponent: String) {
    registerComponent(module, component, ComponentType.instance.Activity, realComponent)
}

fun Navigator.registerServiceComponent(module: String, component: String, realComponent: String) {
    registerComponent(module, component, ComponentType.instance.Service, realComponent)
}

fun Navigator.registerReceiverComponent(context: Context, module: String, component: String, realComponent: String, vararg actions: String) {
    registerComponent(module, component, ComponentType.instance.Receiver, realComponent)
}

fun Navigator.registerProviderComponent(module: String, component: String, realComponent: String) {
    registerComponent(module, component, ComponentType.instance.Provider, realComponent)
}