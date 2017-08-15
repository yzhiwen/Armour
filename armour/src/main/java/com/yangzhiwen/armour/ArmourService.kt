package com.yangzhiwen.armour

import android.app.Service
import android.content.Intent
import android.os.IBinder

/**
 * Created by yangzhiwen on 17/8/15.
 */
class ArmourService : Service() {
    override fun onBind(p0: Intent?): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()
    }

    val map = mutableMapOf<String, Service>()

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        println(" == ArmourService onStartCommand ")

        val component = intent?.getStringExtra("RealComponent") ?: return super.onStartCommand(intent, flags, startId)

        println(" == ArmourService onStartCommand $component")
        // todo 这里可以用函数式
        var service = map[component]
        if (service == null) {
            val aPlugin = Armour.instance(this.application).getPlugin("user_center") ?: return super.onStartCommand(intent, flags, startId)
            service = aPlugin.classloader.loadClass(component).newInstance() as Service
            map[component] = service
        }

        service.onStartCommand(intent, flags, startId)
        return super.onStartCommand(intent, flags, startId)
    }
}