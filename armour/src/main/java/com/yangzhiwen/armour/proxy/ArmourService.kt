package com.yangzhiwen.armour.proxy

import android.app.Service
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import com.yangzhiwen.armour.Armour
import com.yangzhiwen.armour.Hacker
import com.yangzhiwen.armour.ext.helper.parseClassPackage

/**
 * Created by yangzhiwen on 17/8/15.
 */
open class ArmourService : Service() {

    companion object {
        val COMPONENT = "COMPONENT"
        val MODULE_NAME = "MODULE_NAME"
        val ARG_OP = "ARG_OP"
        val START = "START"
        val STOP = "STOP"
        val BIND = "BIND"
        val UNBIND = "UNBIND"
        val SERVICECONNECTION = "SERVICECONNECTION"

        private val armourConnMap = mutableMapOf<String, ServiceConnection>()
        fun getServiceConn(name: String) = armourConnMap[name]
        fun putServiceConn(name: String, conn: ServiceConnection) = armourConnMap.put(name, conn)
        fun removeServiceConn(name: String) = armourConnMap.remove(name)
        fun getConnComponent(conn: ServiceConnection): String? {
            for ((k, v) in armourConnMap.iterator()) {
                if (v === conn) return k
            }
            return null
        }
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

        var service = map[component]
        if (service == null) {
            val aPlugin = Armour.instance(this.application).getPlugin("user_center") ?: return super.onStartCommand(intent, flags, startId)
            // todo try catch
            // todo unbind stop 拦截
            service = aPlugin.aPluginClassloader.loadClass(component).newInstance() as Service
            Hacker.on(service::class.java)
                    .method("attachBaseContext", Context::class.java)
                    ?.invoke(service, this)
            service.onCreate()
            map[component] = service
        }

        val operation = intent.getStringExtra(ARG_OP)
        when (operation) {
            START -> service.onStartCommand(intent, flags, startId)
            STOP -> {
                // 这里不判断是否bind
                service.onDestroy()
                removeServiceConn(component)
                map.remove(component)
            }
            BIND -> {
                // todo 多次调用bindService()，只调用一次onBind()
                val binder = service.onBind(intent)
                val conn = getServiceConn(component) ?: return super.onStartCommand(intent, flags, startId)
                val pn = parseClassPackage(component)
                conn.onServiceConnected(ComponentName(pn, component), binder)
            }
            UNBIND -> {
                service.onUnbind(intent) // todo change real intent
                removeServiceConn(component)
                map.remove(component)
            }
        }
        return super.onStartCommand(intent, flags, startId)
    }
}