package com.yangzhiwen.armour

import android.content.*
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.content.res.AssetManager
import android.content.res.Resources
import android.os.Bundle
import com.yangzhiwen.armour.proxy.ArmourService

/**
 * Created by yangzhiwen on 2017/8/17.
 */
class AContext(val hostContext: Context, val aPlugin: APlugin, val armour: Armour) : ContextWrapper(hostContext) {

    val armourContentResolver = ArmourContentResolver(this, armour)
    val armourHacker = armour.armourHacker

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
        return aPlugin.aPluginResources
    }

    override fun getTheme(): Resources.Theme {
        return super.getTheme()
    }

    // startActivityForResult、startActivity入口分布在Activity ContextImpl InstrumentationApplicationContentResolverApplicationContentResolver
    // 以及以上都会调用 Instrumentation


    // Service相关入口只有ContextImpl一个
    // todo review | try catch | 启动宿主Service
    override fun startService(service: Intent): ComponentName {
        // todo wrap intent
        // target 是否插件Activity （没注册）
        // 是 -> 调用代理Service
        println("${aPlugin.aPluginName} startService $service")
        val componentName = service.component.className ?: return super.startService(service)
        if (armourHacker.onServiceHook(hostContext, componentName, ArmourService.START, {})) return service.component
        return super.startService(service)
    }

    override fun bindService(service: Intent, conn: ServiceConnection, flags: Int): Boolean {
        println("${aPlugin.aPluginName} bindService $service")
        val componentName = service.component.className ?: return super.bindService(service, conn, flags)
        if (armourHacker.onServiceHook(hostContext, componentName, ArmourService.BIND) { ArmourService.putServiceConn(componentName, conn) }) return true
        return super.bindService(service, conn, flags)
    }

    override fun unbindService(conn: ServiceConnection) {
        println("${aPlugin.aPluginName} unbindService")
        val componentName = ArmourService.getConnComponent(conn) ?: return
        if (armourHacker.onServiceHook(hostContext, componentName, ArmourService.UNBIND) { ArmourService.putServiceConn(componentName, conn) }) return
        else super.unbindService(conn)
    }

    override fun stopService(service: Intent): Boolean {
        println("${aPlugin.aPluginName} stopService")
        val componentName = service.component.className ?: return false
        if (armourHacker.onServiceHook(hostContext, componentName, ArmourService.STOP, {})) return true
        else return super.stopService(service)
    }

    // Content Resolver入口只有ContextImpl一个
    override fun getContentResolver(): ContentResolver {
//        todo
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