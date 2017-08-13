package com.yangzhiwen.compass.ext

import android.app.Activity
import android.app.Service
import android.content.BroadcastReceiver
import android.content.ContentProvider
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

fun Navigator.registerReceiverComponent(module: String, component: String, realComponent: String) {
    registerComponent(module, component, ComponentType.instance.Receiver, realComponent)
}

fun Navigator.registerProviderComponent(module: String, component: String, realComponent: String) {
    registerComponent(module, component, ComponentType.instance.Provider, realComponent)
}