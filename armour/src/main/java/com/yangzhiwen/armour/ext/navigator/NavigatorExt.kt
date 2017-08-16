package com.yangzhiwen.armour.ext.navigator

import android.content.Intent
import com.yangzhiwen.armour.ArmourService
import com.yangzhiwen.armour.compass.ComponentType
import com.yangzhiwen.armour.compass.Navigator
import com.yangzhiwen.armour.compass.NavigatorComponent


/**
 * Created by yangzhiwen on 2017/8/12.
 */
// Component Extensions
class ActivityComponent(val isPlugin: Boolean, module: String, component: String, realComponent: String) : NavigatorComponent(module, component, realComponent, ComponentType.instance.Activity)

class ServiceComponent(val isPlugin: Boolean, module: String, component: String, realComponent: String) : NavigatorComponent(module, component, realComponent, ComponentType.instance.Service)

class ReceiverComponent(val isPlugin: Boolean, module: String, component: String, realComponent: String, val actions: Array<out String>) : NavigatorComponent(module, component, realComponent, ComponentType.instance.Receiver)

class ProviderComponent(val isPlugin: Boolean, module: String, component: String, realComponent: String) : NavigatorComponent(module, component, realComponent, ComponentType.instance.Provider)

// Component Register Extensions
fun Navigator.registerActivityComponent(isPlugin: Boolean, module: String, name: String, realComponent: String)
        = registerComponent(ActivityComponent(isPlugin, module, name, realComponent))

fun Navigator.registerServiceComponent(isPlugin: Boolean, module: String, name: String, realComponent: String)
        = registerComponent(ServiceComponent(isPlugin, module, name, realComponent))

fun Navigator.registerReceiverComponent(isPlugin: Boolean, module: String, name: String, realComponent: String, vararg actions: String)
        = registerComponent(ReceiverComponent(isPlugin, module, name, realComponent, actions))

fun Navigator.registerProviderComponent(isPlugin: Boolean, module: String, name: String, realComponent: String)
        = registerComponent(ProviderComponent(isPlugin, module, name, realComponent))

// Activity Extensions
fun Navigator.startActivity(module: String, component: String, jsonArg: String)
        = nav(module, component, "", jsonArg)

// Service Extensions
fun Navigator.startService(module: String, component: String, jsonArg: String)
        = nav(module, component, ArmourService.START, jsonArg)

fun Navigator.stopService(module: String, component: String, jsonArg: String)
        = nav(module, component, ArmourService.STOP, jsonArg)

fun Navigator.bindService(module: String, component: String, jsonArg: String)
        = nav(module, component, ArmourService.BIND, jsonArg)

fun Navigator.unbindService(module: String, component: String, jsonArg: String)
        = nav(module, component, ArmourService.UNBIND, jsonArg)

// Receiver Extensions
fun Navigator.sendBroadcast(intent: Intent)
        = context?.sendBroadcast(intent)
