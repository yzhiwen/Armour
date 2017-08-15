package com.yangzhiwen.armour

import android.app.Service
import android.content.Intent
import android.os.IBinder

/**
 * Created by yangzhiwen on 17/8/15.
 */
class ArmourService : Service() {

    companion object {
        val COMPONENT = "COMPONENT"
        val MODULE_NAME = "MODULE_NAME"
        val ARG_OP = "ARG_OP"
        val START = "START"
        val STOP = "STOP"
        val BIND = "BIND"
        val UNBIND = "UNBIND"
    }

    override fun onBind(p0: Intent?): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()
    }

    val map = mutableMapOf<String, Service>()

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        println(" == ArmourService onStartCommand ")

        val component = intent?.getStringExtra(COMPONENT) ?: return super.onStartCommand(intent, flags, startId)

        println(" == ArmourService onStartCommand $component")
        // todo 这里可以用函数式
        var service = map[component]
        if (service == null) {
            val aPlugin = Armour.instance(this.application).getPlugin("user_center") ?: return super.onStartCommand(intent, flags, startId)
            service = aPlugin.classloader.loadClass(component).newInstance() as Service // todo 需要检查
            service.onCreate()
            map[component] = service
        }

        val operation = intent.getStringExtra(ARG_OP)
        when (operation) {
            START -> service.onStartCommand(intent, flags, startId)
            STOP -> {
                // todo 是否需要判断是否bind
                service.onDestroy()
                map.remove(component)
            }
//            BIND -> service.onBind(intent)
//            UNBIND -> service.unbindService(null)
        }
        return super.onStartCommand(intent, flags, startId)
    }
}