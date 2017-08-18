package com.yangzhiwen.armour

import android.annotation.SuppressLint
import android.app.Application

/**
 * Created by yangzhiwen on 2017/8/13.
 */
class Armour(context: Application) {
    val application = context
    val armourHooker = ArmourHooker()
    val armourInstrumentation: ArmourInstrumentation?
    val classLoaderInterceptor = ArmourClassLoaderInterceptor()
    val armourClassLoader = ArmourClassLoader(application.classLoader)

    init {
        println("init armour")
        ArmourHacker.instance.hackClassLoader(context, armourClassLoader)
        armourInstrumentation = ArmourHacker.instance.hackInstrumentation(this, application)
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