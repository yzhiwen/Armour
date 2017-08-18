package com.yangzhiwen.armour

import android.content.BroadcastReceiver
import android.content.Context
import android.content.IntentFilter
import android.content.res.AssetManager
import android.content.res.Resources
import com.yangzhiwen.armour.ext.compass.Receiver
import com.yangzhiwen.armour.ext.compass.ReceiverComponent
import com.yangzhiwen.armour.compass.ComponentType
import com.yangzhiwen.armour.compass.Navigator
import dalvik.system.DexClassLoader

/**
 * Created by yangzhiwen on 2017/8/13.
 */
class APlugin(hostContext: Context, val aPluginName: String, apkPath: String) {
    val pluginPath = apkPath
    val DEX_OUT_PATH = hostContext.getDir("aplugin", Context.MODE_PRIVATE).absolutePath
    val aPluginClassloader = DexClassLoader(apkPath, DEX_OUT_PATH, null, hostContext.classLoader)

    val aPluginAssetManager: AssetManager
    val aPluginResources: Resources
    val aPluginContextMap = mutableMapOf<String, AContext>()

    init {
        val oldResources = hostContext.resources  //todo 可能为空
        aPluginAssetManager = oldResources.assets.javaClass.newInstance()
        val result = Hacker.on(aPluginAssetManager.javaClass)
                .method("addAssetPath", String::class.java)
                ?.invoke(aPluginAssetManager, apkPath) as Int
        if (result == 0) {
            println("addAssetPath return 0 on the plugin name: $aPluginName")
            // todo throw
        }

        aPluginResources = Resources(aPluginAssetManager, oldResources.displayMetrics, oldResources.configuration)

        val module = Navigator.instance.getModule(aPluginName)
        // todo receiver context res
        // init plugin receiver
        module?.componentMap
                ?.filter { it.value.type == ComponentType.instance.Receiver }
                ?.forEach {
                    val component = it.value as ReceiverComponent
                    val receiver = aPluginClassloader.loadClass(component.realComponent).newInstance() as BroadcastReceiver
                    val filter = IntentFilter()
                    for (action in component.actions) {
                        filter.addAction(action)
                    }
                    hostContext.registerReceiver(receiver, filter)
                }
    }

    fun getAPluginContext(name: String, base: Context): AContext {
        var aPluginContext = aPluginContextMap[name]
        if (aPluginContext == null) {
            aPluginContext = AContext(base, this)
            aPluginContextMap[name] = aPluginContext
        }
        return aPluginContext
    }
}