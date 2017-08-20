package com.yangzhiwen.armour.ext.compass

import android.net.Uri
import com.yangzhiwen.armour.compass.ComponentType
import com.yangzhiwen.armour.compass.Navigator
import com.yangzhiwen.armour.compass.NavigatorComponent
import com.yangzhiwen.armour.compass.NavigatorModule


/**
 * Created by yangzhiwen on 2017/8/12.
 */

// Component Type Extensions
val ComponentType.Activity: String get() = "ActivityType"

val ComponentType.Service: String get() = "ServiceType"

val ComponentType.Receiver: String get() = "ReceiverType"

val ComponentType.Provider: String get() = "ProviderType"

// Component Extensions
class ActivityComponent(module: String, component: String, realComponent: String, val isPlugin: Boolean)
    : NavigatorComponent(module, component, realComponent, ComponentType.instance.Activity)

class ServiceComponent(module: String, component: String, realComponent: String, val isRemote: Boolean, val isPlugin: Boolean)
    : NavigatorComponent(module, component, realComponent, ComponentType.instance.Service)

class ReceiverComponent(module: String, component: String, realComponent: String, val actions: Array<out String>, val isPlugin: Boolean)
    : NavigatorComponent(module, component, realComponent, ComponentType.instance.Receiver)

class ProviderComponent(module: String, component: String, realComponent: String, var url: Uri, val isPlugin: Boolean)
    : NavigatorComponent(module, component, realComponent, ComponentType.instance.Provider)

// Component Register Extensions
fun NavigatorModule.registerActivityComponent(name: String, realComponent: String)
        = registerComponent(ActivityComponent(moduleName, name, realComponent, isPlugin))

fun NavigatorModule.registerServiceComponent(name: String, realComponent: String, isRemote: Boolean = true)
        = registerComponent(ServiceComponent(moduleName, name, realComponent, isRemote, isPlugin))

fun NavigatorModule.registerReceiverComponent(name: String, realComponent: String, vararg actions: String)
        = registerComponent(ReceiverComponent(moduleName, name, realComponent, actions, isPlugin))

fun NavigatorModule.registerProviderComponent(name: String, realComponent: String, url: Uri)
        = registerComponent(ProviderComponent(moduleName, name, realComponent, url, isPlugin))