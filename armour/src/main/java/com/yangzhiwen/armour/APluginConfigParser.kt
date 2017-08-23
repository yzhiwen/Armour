package com.yangzhiwen.armour

import android.content.res.AssetManager
import android.net.Uri
import com.yangzhiwen.armour.compass.Navigator
import com.yangzhiwen.armour.ext.compass.registerActivityComponent
import com.yangzhiwen.armour.ext.compass.registerProviderComponent
import com.yangzhiwen.armour.ext.compass.registerReceiverComponent
import com.yangzhiwen.armour.ext.compass.registerServiceComponent
import org.json.JSONObject

/**
 * Created by yangzhiwen on 17/8/21.
 */
class APluginConfigParser {
    companion object {
        val instance = APluginConfigParser()
        val MODULE = "module"
        val ACTIVITY = "activity"
        val SERVICE = "service"
        val RECEIVER = "receiver"
        val PROVIDER = "provider"
        val NAME = "name"
        val MAIN_ACTIVITY = "isMain"
        val URL = "url"
        val ACTION = "action"
        val REMOTE = "remote"
        val CONFIG = "ArmourConfig.json" // todo change name
    }


    fun parseConfig(aPluginAssetManager: AssetManager): String? {
        var aMainActivity: String? = null
        val inputStream = aPluginAssetManager.open(CONFIG)
        val size = inputStream.available()
        val bytes = ByteArray(size)
        inputStream.read(bytes)
        inputStream.close()
        val string = String(bytes)
        val json = JSONObject(string)
        val module = json.getString(MODULE)

        Navigator.instance.register(module, true) {
            var index = 0
            val activityList = json.getJSONArray(ACTIVITY)
            while (index < activityList.length()) {
                val activityObject = activityList.getJSONObject(index++)
                val name = activityObject.getString(NAME)
                val main = activityObject.getBoolean(MAIN_ACTIVITY)
                if (main) aMainActivity = name
                registerActivityComponent(name, name)
            }

            index = 0
            val serviceList = json.getJSONArray(SERVICE)
            while (index < serviceList.length()) {
                val serviceObj = serviceList.getJSONObject(index++)
                val name = serviceObj.getString(NAME)
                val remote = serviceObj.getBoolean(REMOTE)
                registerServiceComponent(name, name, remote)
            }

            index = 0
            val receiverList = json.getJSONArray(RECEIVER)
            while (index < receiverList.length()) {
                val receiverObj = receiverList.getJSONObject(index++)
                val name = receiverObj.getString(NAME)
                val action = receiverObj.getJSONArray(ACTION)
                val actions = emptyArray<String>()
                var aIndex = 0
                while (aIndex < action.length()) actions.plus(action.getString(aIndex++))
                registerReceiverComponent(name, name, *actions)
            }

            index = 0
            val providerList = json.getJSONArray(PROVIDER)
            while (index < providerList.length()) {
                val providerObj = providerList.getJSONObject(index++)
                val name = providerObj.getString(NAME)
                val url = providerObj.getString(URL)
                registerProviderComponent(name, name, Uri.parse(url))
            }
        }
        return aMainActivity
    }
}