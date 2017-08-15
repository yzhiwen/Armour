package com.yangzhiwen.navigator.ext.navigator

import android.content.ComponentName
import android.content.Intent
import android.os.Bundle
import com.yangzhiwen.armour.ArmourService
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

        // component 是否是插件
        // component 关联（匹配） 宿主组件
        // 启动宿主组件
        // ClassLoader load的时候 宿主组件替换 插件Component

        if (component.component.equals("center")) {
            val intent = Intent()
            intent.component = ComponentName("com.yangzhiwen.navigator", "com.yangzhiwen.navigator.ProxyActivity")
            println("$intent")
            println("${intent.component}")
            println("${intent.extras}")
            Navigator.instance.context?.startActivity(intent)
            return
        }

        val intent = Intent()
        intent.component = ComponentName("com.yangzhiwen.navigator", component.realComponent)
        Navigator.instance.context?.startActivity(intent)
    }
}

class ServiceComponentHandler : NavigatorComponentHandler(ComponentType.instance.Service) {
    companion object {
        val instance = ServiceComponentHandler()
    }

    override fun onHandle(component: NavigatorComponent, jsonArg: String) {
        println("On Service Handle() :: $component arg : $jsonArg")

        // component 是否是插件
        // 启动本地用于代理Component的Service
        if (component.component == "user_service") {
            val context = Navigator.instance.context ?: return
            val intent = Intent(context, ArmourService::class.java)
//            intent.component = ComponentName("com.yangzhiwen.armour", "com.yangzhiwen.armour.ArmourService") // 不知道为什么启动不了

            println("$intent")
            println("${intent.component}")
            println("${intent.extras}")

            intent.putExtra("RealComponent", component.realComponent)
            context.startService(intent)
            return
        }

        val intent = Intent()
        intent.component = ComponentName("com.yangzhiwen.navigator", component.realComponent)
        Navigator.instance.context?.startService(intent)
    }

}

fun Navigator.registerActivityComponentHandler() {
    registerComponentHandler(ComponentType.instance.Activity, ActivityComponentHandler.instance)
}

fun Navigator.registerServiceComponentHandler() {
    registerComponentHandler(ComponentType.instance.Service, ServiceComponentHandler.instance)
}

