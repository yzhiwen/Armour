package com.yangzhiwen.armour

import android.annotation.SuppressLint
import android.app.Application

/**
 * Created by yangzhiwen on 2017/8/13.
 */
class Armour(context: Application) {
    val application = context
    val armourHacker = ArmourHacker(application)
    val armourInstrumentation: ArmourInstrumentation
    val armourIContentProvider: Any?
//    val armourClassLoader = ArmourClassLoader(application.classLoader)

    init {
        println("init armour")
//        armourHacker.hackClassLoader(context, armourClassLoader)
        armourInstrumentation = armourHacker.hackInstrumentation(this, application)
        armourIContentProvider = armourHacker.hackContentProvider(application)
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
        if (map[name] == null) map[name] = APlugin(application, name, path, this)
        return map[name]
    }

    fun getPlugin(name: String) = map[name]
}