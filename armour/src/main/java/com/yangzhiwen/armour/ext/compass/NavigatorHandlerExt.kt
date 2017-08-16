package com.yangzhiwen.armour.ext.compass

import android.content.ComponentName
import android.content.Intent
import com.yangzhiwen.armour.Armour
import com.yangzhiwen.armour.ArmourService
import com.yangzhiwen.armour.compass.ComponentType
import com.yangzhiwen.armour.compass.Navigator
import com.yangzhiwen.armour.compass.NavigatorComponent
import com.yangzhiwen.armour.compass.NavigatorComponentHandler
import com.yangzhiwen.armour.ext.helper.parseClassName

/**
 * Created by yangzhiwen on 2017/8/12.
 */

class ActivityComponentHandler : NavigatorComponentHandler(ComponentType.instance.Activity) {

    companion object {
        val instance = ActivityComponentHandler()
    }

    override fun onHandle(component: NavigatorComponent, operation: String, jsonArg: String) {
        println("On Activity Handle() :: " + component.name + " arg : " + jsonArg)

        // component 是否是插件
        // component 关联（匹配） 宿主组件
        // 启动宿主组件
        // ClassLoader load的时候 宿主组件替换 插件Component

        // 插件 组件  关联 宿主 组件
        if (component is ActivityComponent && component.isPlugin) {
            // todo component 匹配 占坑组件
            val proxy = "com.yangzhiwen.navigator.ProxyActivity"
            val proxyPair = parseClassName(proxy)
            Armour.instance()?.classLoaderInterceptor?.addLoadInterceptor(proxy, component)
            val intent = Intent()
            // todo 占坑组件
            intent.component = ComponentName(proxyPair.first, proxy)
            Navigator.instance.context?.startActivity(intent)
            return
        } else {
            val intent = Intent()
            val proxyPair = parseClassName(component.realComponent)
            intent.component = ComponentName(proxyPair.first, component.realComponent)
            Navigator.instance.context?.startActivity(intent)
        }
    }
}

class ServiceComponentHandler : NavigatorComponentHandler(ComponentType.instance.Service) {
    companion object {
        val instance = ServiceComponentHandler()
    }

    override fun onHandle(component: NavigatorComponent, operation: String, jsonArg: String) {
        println("On Service Handle() :: $component arg :: $operation :: $jsonArg")

        // component 是否是插件
        // 启动本地用于代理Component的Service
        if (component.name == "user_service") {
            val context = Navigator.instance.context ?: return
            val intent = Intent(context, ArmourService::class.java)
//            intent.component = ComponentName("com.yangzhiwen.armour", "com.yangzhiwen.armour.ArmourService") // 不知道为什么启动不了

            intent.putExtra(ArmourService.COMPONENT, component.realComponent)
            intent.putExtra(ArmourService.ARG_OP, operation)

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
