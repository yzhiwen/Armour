package com.yangzhiwen.armour

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.res.AssetManager
import android.content.res.Resources
import android.net.Uri
import com.yangzhiwen.armour.compass.ComponentType
import com.yangzhiwen.armour.compass.Navigator
import com.yangzhiwen.armour.ext.Hacker
import com.yangzhiwen.armour.ext.compass.*
import dalvik.system.DexClassLoader
import org.json.JSONObject

/**
 * Created by yangzhiwen on 2017/8/13.
 */
class APlugin(hostContext: Context, val aPluginName: String, apkPath: String, val armour: Armour) {
    val DEX_OUT_PATH = hostContext.getDir("aplugin", Context.MODE_PRIVATE).absolutePath!!
    val aPluginClassloader = DexClassLoader(apkPath, DEX_OUT_PATH, null, hostContext.classLoader)

    val aMainActivity: String?

    val aPluginAssetManager: AssetManager
    val aPluginResources: Resources
    val aPluginContextMap = mutableMapOf<String, AContext>()
    val aPluginContext = AContext(hostContext, this, armour)

    init {
        val oldResources = hostContext.resources
        aPluginAssetManager = oldResources.assets.javaClass.newInstance()
        val result = Hacker.on(aPluginAssetManager.javaClass)
                .declaredMethod("addAssetPath", String::class.java)!!.invoke(aPluginAssetManager, apkPath) as Int
        if (result == 0) {
            throw ArmourException("addAssetPath return 0 on the plugin name: $aPluginName")
        }

        aPluginResources = Resources(aPluginAssetManager, oldResources.displayMetrics, oldResources.configuration)

        aMainActivity = parseConfig()

        val module = Navigator.instance.getModule(aPluginName)
        // receiver application context
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

    // todo 异步 try catch
    private fun parseConfig(): String? {
        var aMainActivity: String? = null
        val inputStream = aPluginAssetManager.open(ArmourConfig.CONFIG)
        val size = inputStream.available()
        val bytes = ByteArray(size)
        inputStream.read(bytes)
        inputStream.close()
        val string = String(bytes)
        val json = JSONObject(string)
        val module = json.getString(ArmourConfig.MODULE)

        Navigator.instance.register(module, true) {
            var index = 0
            val activityList = json.getJSONArray(ArmourConfig.ACTIVITY)
            while (index < activityList.length()) {
                val activityObject = activityList.getJSONObject(index++)
                val name = activityObject.getString(ArmourConfig.NAME)
                val main = activityObject.getBoolean(ArmourConfig.MAIN_ACTIVITY)
                if (main) aMainActivity = name
                registerActivityComponent(name, name)
            }

            index = 0
            val serviceList = json.getJSONArray(ArmourConfig.SERVICE)
            while (index < serviceList.length()) {
                val serviceObj = serviceList.getJSONObject(index++)
                val name = serviceObj.getString(ArmourConfig.NAME)
                val remote = serviceObj.getBoolean(ArmourConfig.REMOTE)
                registerServiceComponent(name, name, remote)
            }

            index = 0
            val receiverList = json.getJSONArray(ArmourConfig.RECEIVER)
            while (index < receiverList.length()) {
                val receiverObj = receiverList.getJSONObject(index++)
                val name = receiverObj.getString(ArmourConfig.NAME)
                val action = receiverObj.getJSONArray(ArmourConfig.ACTION)
                val actions = emptyArray<String>()
                var aIndex = 0
                while (aIndex < action.length()) actions.plus(action.getString(aIndex++))
                registerReceiverComponent(name, name, *actions)
            }

            index = 0
            val providerList = json.getJSONArray(ArmourConfig.PROVIDER)
            while (index < providerList.length()) {
                val providerObj = providerList.getJSONObject(index++)
                val name = providerObj.getString(ArmourConfig.NAME)
                val url = providerObj.getString(ArmourConfig.URL)
                registerProviderComponent(name, name, Uri.parse(url))
            }
        }
        return aMainActivity
    }

    fun start(): Boolean {
        if (aMainActivity == null) return false
        else aPluginContext.startActivity(Intent(aPluginContext, aPluginClassloader.loadClass(aMainActivity)))
        return true
    }

    fun getAPluginContext(name: String, base: Context): AContext {
        var aPluginContext = aPluginContextMap[name]
        if (aPluginContext == null) {
            aPluginContext = AContext(base, this, armour)
            aPluginContextMap.put(name, aPluginContext)
        }
        return aPluginContext
    }
}