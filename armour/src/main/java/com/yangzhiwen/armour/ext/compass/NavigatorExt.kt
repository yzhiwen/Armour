package com.yangzhiwen.armour.ext.compass

import android.content.ContentValues
import android.content.Intent
import android.content.ServiceConnection
import android.database.Cursor
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

// Activity Extensions
fun Navigator.startActivity(module: String, component: String, jsonArg: String)
        = nav(module, component, StartActivityOperation.instance, jsonArg)

// Service Extensions
fun Navigator.startService(module: String, component: String, jsonArg: String)
        = nav(module, component, StartServiceOperation.instance, jsonArg)

fun Navigator.stopService(module: String, component: String, jsonArg: String)
        = nav(module, component, StopServiceOperation.instance, jsonArg)

fun Navigator.bindService(module: String, component: String, sc: ServiceConnection, jsonArg: String) {
//    val cmp = getModule(module)?.getComponent(component) ?: return
//    if (cmp is ServiceComponent) cmp.sc = sc
    nav(module, component, BindServiceOperation(sc), jsonArg)
}

fun Navigator.unbindService(module: String, component: String, sc: ServiceConnection, jsonArg: String)
        = nav(module, component, UnbindServiceOperation(sc), jsonArg)

// Receiver Extensions
fun Navigator.sendBroadcast(intent: Intent)
        = context?.sendBroadcast(intent)

// Provider Extensions
fun Navigator.insert(module: String, component: String, url: Uri, values: ContentValues)
        = nav(module, component, InsertContentOperation(url, values))

//Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder
fun Navigator.query(module: String, component: String, url: Uri, projection: Array<out String>?, selection: String?, selectionArgs: Array<out String>?, sortOrder: String, callback: (c: Cursor?) -> Unit)
        = nav(module, component, QueryContentOperation(url, projection, selection, selectionArgs, sortOrder, callback))

//Uri url, String where, String[] selectionArgs
fun Navigator.delete(module: String, component: String, url: Uri, where: String?, selectionArgs: Array<out String>?)
        = nav(module, component, DeleteContentOperation(url, where, selectionArgs))
