package com.yangzhiwen.navigator.ext.navigator

import android.content.Context
import android.content.Intent
import com.yangzhiwen.armour.ArmourService
import com.yangzhiwen.compass.ComponentType
import com.yangzhiwen.compass.Navigator
import com.yangzhiwen.compass.NavigatorComponent


/**
 * Created by yangzhiwen on 2017/8/12.
 */

class ActivityComponent(val isPlugin: Boolean, module: String, component: String, realComponent: String, type: String) : NavigatorComponent(module, component, realComponent, type)

class ServiceComponent(val isPlugin: Boolean, module: String, component: String, realComponent: String, type: String) : NavigatorComponent(module, component, realComponent, type)

fun Navigator.registerActivityComponent(isPlugin: Boolean, module: String, component: String, realComponent: String) {
    registerComponent(ActivityComponent(isPlugin, module, component, realComponent, ComponentType.instance.Activity))
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

// Activity 扩展
fun Navigator.startActivity(module: String, component: String, jsonArg: String)
        = nav(module, component, "", jsonArg)

// Service 扩展
fun Navigator.startService(module: String, component: String, jsonArg: String)
        = nav(module, component, ArmourService.START, jsonArg)

fun Navigator.stopService(module: String, component: String, jsonArg: String)
        = nav(module, component, ArmourService.STOP, jsonArg)

fun Navigator.bindService(module: String, component: String, jsonArg: String)
        = nav(module, component, ArmourService.BIND, jsonArg)

fun Navigator.unbindService(module: String, component: String, jsonArg: String)
        = nav(module, component, ArmourService.UNBIND, jsonArg)

// Receiver 扩展
fun Navigator.sendBroadcast(intent: Intent) {
    context?.sendBroadcast(intent)
}