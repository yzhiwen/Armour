package com.yangzhiwen.armour.ext.compass

import android.content.ComponentName
import android.content.Intent
import com.yangzhiwen.armour.Armour
import com.yangzhiwen.armour.proxy.ArmourService
import com.yangzhiwen.armour.compass.*
import com.yangzhiwen.armour.ext.helper.parseClassPackage
import com.yangzhiwen.armour.ext.helper.wrapUrl
import com.yangzhiwen.armour.proxy.ArmourActivity
import com.yangzhiwen.armour.proxy.ArmourRemoteService

/**
 * Created by yangzhiwen on 2017/8/12.
 */

class ActivityComponentHandler : NavigatorComponentHandler(ComponentType.instance.Activity) {

    companion object {
        val instance = ActivityComponentHandler()
    }

    override fun onHandle(component: NavigatorComponent, operation: ComponentOperation, jsonArg: String) {
        println("On Activity Handle() :: " + component.name + " arg : " + jsonArg)
        val context = Navigator.instance.context ?: return
        // component 是否是插件
        // component 关联（匹配） 宿主组件
        // 启动宿主组件
        // ClassLoader load的时候 宿主组件替换 插件Component

        // 插件 组件  关联 宿主 组件
        if (component is ActivityComponent && component.isPlugin) {
            // todo component 匹配 占坑组件
            val proxy = "com.yangzhiwen.armour.proxy.ArmourActivity"
            Armour.instance()?.classLoaderInterceptor?.addLoadInterceptor(proxy, component)
//             todo 占坑组件w
            context.startActivity(Intent(context, ArmourActivity::class.java)) // 隐式启动android library Activity 会报错：android.content.ActivityNotFoundException: Unable to find explicit activity class
            return
        } else {
            val intent = Intent()
            val pn = parseClassPackage(component.realComponent)
            intent.component = ComponentName(pn, component.realComponent)
            context.startActivity(intent)
        }
    }
}

// todo 首先这里有个问题，如果要在宿主操作插件的ServiceConnection，那么就存在耦合，而且插件Service本身类可能不存在宿主，所以这是一种规范，所以最好不要在宿主中启动插件Service
class ServiceComponentHandler : NavigatorComponentHandler(ComponentType.instance.Service) {
    companion object {
        val instance = ServiceComponentHandler()
    }

    override fun onHandle(component: NavigatorComponent, operation: ComponentOperation, jsonArg: String) {
        println("On Service Handle() :: $component arg :: $operation :: $jsonArg")
        if (component !is ServiceComponent) return
        val context = Navigator.instance.context ?: return
//      intent.component = ComponentName("com.yangzhiwen.armour", "com.yangzhiwen.armour.proxy.ArmourService") // Android5.x之后必须使用显式Intent调用Service

        if (component.isPlugin) {
            val intent: Intent
            if (component.isRemote) intent = Intent(context, ArmourRemoteService::class.java)
            else intent = Intent(context, ArmourService::class.java)

            intent.putExtra(ArmourService.COMPONENT, component.realComponent)
            intent.putExtra(ArmourService.ARG_OP, operation.opt)
            context.startService(intent)
        } else {
            val intent = Intent(context, javaClass.classLoader.loadClass(component.realComponent))
            when (operation) {
                is StartServiceOperation -> context.startService(intent)
                is StopServiceOperation -> context.stopService(intent)
                is BindServiceOperation -> context.bindService(intent, operation.sc, 0)
                is UnbindServiceOperation -> context.unbindService(operation.sc)
            }
        }
    }
}

class ContentProviderComponentHandler : NavigatorComponentHandler(ComponentType.instance.Provider) {
    companion object {
        val instance = ContentProviderComponentHandler()
    }

    override fun onHandle(component: NavigatorComponent, operation: ComponentOperation, jsonArg: String) {
        if (component !is ProviderComponent) return
        val context = Navigator.instance.context ?: return
        println("On Provider Handle $component : $operation")

        if (component.isPlugin) {
            when (operation) {
                is InsertContentOperation -> operation.url = wrapUrl(component, operation.url)
                is DeleteContentOperation -> operation.url = wrapUrl(component, operation.url)
                is QueryContentOperation -> operation.url = wrapUrl(component, operation.url)
            }
        }

        when (operation) {
            is InsertContentOperation -> context.contentResolver.insert(operation.url, operation.values)
            is DeleteContentOperation -> context.contentResolver.delete(operation.url, operation.where, operation.selectionArgs)
            is QueryContentOperation -> operation.callback(context.contentResolver.query(operation.url, operation.projection, operation.selection, operation.selectionArgs, operation.sortOrder))
        }
    }
}

fun Navigator.registerActivityComponentHandler()
        = registerComponentHandler(ComponentType.instance.Activity, ActivityComponentHandler.instance)

fun Navigator.registerServiceComponentHandler()
        = registerComponentHandler(ComponentType.instance.Service, ServiceComponentHandler.instance)

fun Navigator.registerProviderComponentHandler()
        = registerComponentHandler(ComponentType.instance.Provider, ContentProviderComponentHandler.instance)