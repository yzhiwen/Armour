package com.yangzhiwen.armour

import android.annotation.SuppressLint
import android.app.Application
import java.lang.reflect.Proxy

/**
 * Created by yangzhiwen on 2017/8/13.
 */
class Armour(context: Application) {
    val application = context
    val armourHooker = ArmourHooker()
    val armourInstrumentation: ArmourInstrumentation?
    val armourIContentProvider: Any?
    val classLoaderInterceptor = ArmourClassLoaderInterceptor()
    val armourClassLoader = ArmourClassLoader(application.classLoader)

    init {
        println("init armour")
        ArmourHacker.instance.hackClassLoader(context, armourClassLoader)
        armourInstrumentation = ArmourHacker.instance.hackInstrumentation(this, application)
        armourIContentProvider = hackContentProvider()
    }

    private fun hackContentProvider(): Any? {
        val activityThread = Hacker.on(application.baseContext.javaClass)
                .field("mMainThread")
                ?.get(application.baseContext) ?: return null

        val mProviderMap = Hacker.on(activityThread.javaClass)
                .field("mProviderMap")
                ?.get(activityThread) as Map<*, *>

        var icp: Any? = null

        for ((k, v) in mProviderMap) {
            if (k == null || v == null) continue
            val authority = Hacker.on(k.javaClass)
                    .field("authority")
                    ?.get(k) as String
            if (authority == "com.yangzhiwen.armour") {
                icp = Hacker.on(v.javaClass)
                        .field("mProvider")
                        ?.get(v)
                break
            }
        }

        val cl = arrayOf(Class.forName("android.content.IContentProvider"))
        val proxy = Proxy.newProxyInstance(application.classLoader, cl, ArmourIContentProvider(icp))

        return proxy
    }

    companion object {
        @SuppressLint("StaticFieldLeak")
        private var ins: Armour? = null

        fun instance(context: Application): Armour {
            if (ins == null) ins = Armour(context)
            return ins as Armour
        }

        fun instance() = ins
    }

    val map = mutableMapOf<String, APlugin>()
    fun instantPlugin(name: String, path: String): APlugin? {
        if (map[name] == null) map[name] = APlugin(application, name, path)
        return map[name]
    }

    fun getPlugin(name: String) = map[name]
}