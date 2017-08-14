package com.yangzhiwen.navigator.ext.navigator

import android.content.ComponentName
import android.content.Intent
import com.yangzhiwen.compass.ComponentType
import com.yangzhiwen.compass.Navigator
import com.yangzhiwen.compass.NavigatorComponent
import com.yangzhiwen.compass.NavigatorComponentHandler

/**
 * Created by yangzhiwen on 2017/8/12.
 */

class ActivityComponentHandler : NavigatorComponentHandler(ComponentType.instance.Activity) {

    companion object {
        val instance = ActivityComponentHandler()
    }

    override fun onHandle(component: NavigatorComponent, jsonArg: String) {
        println("On Activity Handle() :: " + component.component + " arg : " + jsonArg)

        if (component.component.equals("center")) {
            val intent = Intent()
            intent.component = ComponentName("com.yangzhiwen.navigator", "com.yangzhiwen.navigator.ProxyActivity")
            Navigator.instance.context?.startActivity(intent)
            return
        }

        val intent = Intent()
        intent.component = ComponentName("com.yangzhiwen.navigator", component.realComponent)
        Navigator.instance.context?.startActivity(intent)
    }
}

fun Navigator.registerActivityComponentHandler() {
    registerComponentHandler(ComponentType.instance.Activity, ActivityComponentHandler.instance)
}