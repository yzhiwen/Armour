package com.yangzhiwen.armour.ext.compass

import android.net.Uri
import com.yangzhiwen.armour.compass.ComponentType
import com.yangzhiwen.armour.compass.Navigator
import com.yangzhiwen.armour.compass.NavigatorComponent


/**
 * Created by yangzhiwen on 2017/8/12.
 */
// Component Extensions
class ActivityComponent(val isPlugin: Boolean, module: String, component: String, realComponent: String) : NavigatorComponent(module, component, realComponent, ComponentType.instance.Activity)

class ServiceComponent(val isPlugin: Boolean, module: String, component: String, realComponent: String, val isRemote: Boolean) : NavigatorComponent(module, component, realComponent, ComponentType.instance.Service)

class ReceiverComponent(val isPlugin: Boolean, module: String, component: String, realComponent: String, val actions: Array<out String>) : NavigatorComponent(module, component, realComponent, ComponentType.instance.Receiver)

class ProviderComponent(val isPlugin: Boolean, module: String, component: String, realComponent: String, var url: Uri) : NavigatorComponent(module, component, realComponent, ComponentType.instance.Provider)

// Component Register Extensions
fun Navigator.registerActivityComponent(isPlugin: Boolean, module: String, name: String, realComponent: String)
        = registerComponent(ActivityComponent(isPlugin, module, name, realComponent))

fun Navigator.registerServiceComponent(isPlugin: Boolean, module: String, name: String, realComponent: String, isRemote: Boolean)
        = registerComponent(ServiceComponent(isPlugin, module, name, realComponent, isRemote))

fun Navigator.registerReceiverComponent(isPlugin: Boolean, module: String, name: String, realComponent: String, vararg actions: String)
        = registerComponent(ReceiverComponent(isPlugin, module, name, realComponent, actions))

fun Navigator.registerProviderComponent(isPlugin: Boolean, module: String, name: String, realComponent: String, url: Uri)
        = registerComponent(ProviderComponent(isPlugin, module, name, realComponent, url))