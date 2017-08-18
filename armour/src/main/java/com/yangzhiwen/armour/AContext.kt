package com.yangzhiwen.armour

import android.content.*
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.content.res.AssetManager
import android.content.res.Resources
import android.os.Bundle
import com.yangzhiwen.armour.compass.Navigator
import com.yangzhiwen.armour.compass.NavigatorComponent
import com.yangzhiwen.armour.ext.compass.ServiceComponent
import com.yangzhiwen.armour.proxy.ArmourRemoteService
import com.yangzhiwen.armour.proxy.ArmourService

/**
 * Created by yangzhiwen on 2017/8/17.
 */
class AContext(val hostContext: Context, val aPlugin: APlugin) : ContextWrapper(hostContext) {

    val armourContentResolver = ArmourContentResolver(this)

    override fun getApplicationContext(): Context {
        return super.getApplicationContext()
    }

    override fun getApplicationInfo(): ApplicationInfo {
        return super.getApplicationInfo()
    }

    override fun getAssets(): AssetManager {
        return aPlugin.aPluginAssetManager
    }

    override fun getBaseContext(): Context {
        return super.getBaseContext()
    }

    override fun getClassLoader(): ClassLoader {
        return aPlugin.aPluginClassloader
    }

    override fun getPackageManager(): PackageManager {
        return super.getPackageManager()
    }

    override fun getPackageName(): String {
        return super.getPackageName()
    }

    override fun getResources(): Resources {
        return super.getResources()
    }

    override fun getTheme(): Resources.Theme {
        return super.getTheme()
    }

    // startActivityForResult、startActivity入口分布在Activity ContextImpl Instrumentation
    // 以及以上都会调用 Instrumentation

    fun onHandle(component: NavigatorComponent, operation: String, connOperation: () -> Unit): Boolean {
        if (component !is ServiceComponent) return false
        if (!component.isPlugin) return false

        val intent: Intent
        if (component.isRemote) intent = Intent(hostContext, ArmourRemoteService::class.java)
        else intent = Intent(hostContext, ArmourService::class.java)

        intent.putExtra(ArmourService.COMPONENT, component.realComponent)
        intent.putExtra(ArmourService.ARG_OP, operation)
        connOperation()
        hostContext.startService(intent)
        return true
    }

    // Service相关入口只有ContextImpl一个
    // todo review | try catch | 启动宿主Service
    override fun startService(service: Intent): ComponentName {
        // todo wrap intent
        // target 是否插件Activity （没注册）
        // 是 -> 调用代理Service
        println("${aPlugin.aPluginName} startService $service")
        val componentName = service.component.className ?: return super.startService(service)
        val component = Navigator.instance.getComponentByRealComponent(componentName) ?: return super.startService(service)
        if (onHandle(component, ArmourService.START, {})) return service.component
        return super.startService(service)
    }

    override fun bindService(service: Intent, conn: ServiceConnection, flags: Int): Boolean {
        println("${aPlugin.aPluginName} bindService $service")
        val componentName = service.component.className ?: return super.bindService(service, conn, flags)
        val component = Navigator.instance.getComponentByRealComponent(componentName) ?: return super.bindService(service, conn, flags)
        if (onHandle(component, ArmourService.BIND) { ArmourService.putServiceConn(componentName, conn) }) return true
        return super.bindService(service, conn, flags)
    }

    override fun unbindService(conn: ServiceConnection) {
        println("${aPlugin.aPluginName} unbindService")
        val componentName = ArmourService.getConnComponent(conn) ?: return
        val component = Navigator.instance.getComponentByRealComponent(componentName) ?: return
        if (onHandle(component, ArmourService.UNBIND) { ArmourService.putServiceConn(componentName, conn) }) return
        else super.unbindService(conn)
    }

    override fun stopService(service: Intent): Boolean {
        println("${aPlugin.aPluginName} stopService")
        val componentName = service.component.className ?: return false
        val component = Navigator.instance.getComponentByRealComponent(componentName) ?: return false
        if (onHandle(component, ArmourService.STOP, {})) return true
        else return super.stopService(service)
    }

    // Content Resolver入口只有ContextImpl一个
    override fun getContentResolver(): ContentResolver {
        println("${aPlugin.aPluginName} getContentResolver")
        return armourContentResolver
    }

    // todo IntentSender ??
    override fun startIntentSender(intent: IntentSender?, fillInIntent: Intent?, flagsMask: Int, flagsValues: Int, extraFlags: Int, options: Bundle?) {
        super.startIntentSender(intent, fillInIntent, flagsMask, flagsValues, extraFlags, options)
    }

    override fun startForegroundService(service: Intent?): ComponentName {
        return super.startForegroundService(service)
    }
}