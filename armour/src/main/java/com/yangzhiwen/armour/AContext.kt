package com.yangzhiwen.armour

import android.content.*
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.content.res.AssetManager
import android.content.res.Resources
import android.os.Bundle

/**
 * Created by yangzhiwen on 2017/8/17.
 */
class AContext(hostContext: Context, val aPlugin: APlugin) : ContextWrapper(hostContext) {

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

    // Service相关入口只有ContextImpl一个
    override fun startService(service: Intent?): ComponentName {
        // todo wrap intent
        // target 是否插件Activity （没注册）
        // 是 -> 调用代理Service
        return super.startService(service)
    }

    override fun bindService(service: Intent?, conn: ServiceConnection?, flags: Int): Boolean {
        return super.bindService(service, conn, flags)
    }

    override fun unbindService(conn: ServiceConnection?) {
        super.unbindService(conn)
    }

    override fun stopService(name: Intent?): Boolean {
        return super.stopService(name)
    }

    // Content Resolver入口只有ContextImpl一个
    override fun getContentResolver(): ContentResolver {
        return super.getContentResolver()
    }

    // todo IntentSender ??
    override fun startIntentSender(intent: IntentSender?, fillInIntent: Intent?, flagsMask: Int, flagsValues: Int, extraFlags: Int, options: Bundle?) {
        super.startIntentSender(intent, fillInIntent, flagsMask, flagsValues, extraFlags, options)
    }

    override fun startForegroundService(service: Intent?): ComponentName {
        return super.startForegroundService(service)
    }

}