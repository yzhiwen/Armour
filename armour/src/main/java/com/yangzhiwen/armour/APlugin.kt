package com.yangzhiwen.armour

import android.content.BroadcastReceiver
import android.content.Context
import android.content.IntentFilter
import com.yangzhiwen.armour.ext.compass.Receiver
import com.yangzhiwen.armour.ext.compass.ReceiverComponent
import com.yangzhiwen.armour.compass.ComponentType
import com.yangzhiwen.armour.compass.Navigator
import dalvik.system.DexClassLoader

/**
 * Created by yangzhiwen on 2017/8/13.
 */
class APlugin(context: Context, name: String, path: String) {
    val pluginPath = path
    val DEX_OUT_PATH = context.getDir("aplugin", Context.MODE_PRIVATE).absolutePath
    val classloader = DexClassLoader(path, DEX_OUT_PATH, null, context.classLoader)

    init {
        val moudle = Navigator.instance.getModule(name)
        val map = moudle?.componentMap
        map?.iterator()?.forEach { (k, v) -> run { println("$k,$v") } }


        val module = Navigator.instance.getModule(name)
        // init plugin receiver
        module?.componentMap
                ?.filter { it -> it.value.type == ComponentType.instance.Receiver }
                ?.forEach {
                    it ->
                    val component = it.value as ReceiverComponent
                    val receiver = classloader.loadClass(component.realComponent).newInstance() as BroadcastReceiver
                    val filter = IntentFilter()
                    for (action in component.actions) {
                        filter.addAction(action)
                    }
                    context.registerReceiver(receiver, filter)
                }
    }

}