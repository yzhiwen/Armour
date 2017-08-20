package com.yangzhiwen.armour.proxy

import android.app.Application
import android.app.Service
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import com.yangzhiwen.armour.Armour
import com.yangzhiwen.armour.ext.Hacker
import com.yangzhiwen.armour.ext.parseClassPackage

/**
 * Created by yangzhiwen on 17/8/15.
 */
open class ArmourService : Service() {

    val mToken by lazy {
        Hacker.on(Service::class.java)
                .field("mToken")
                ?.get(this)
    }

    val mActivityManager by lazy {
        Hacker.on(Service::class.java)
                .field("mActivityManager")
                ?.get(this)
    }

    val mClassName by lazy {
        Hacker.on(Service::class.java)
                .field("mClassName")
                ?.get(this)
    }

    val mThread by lazy { Armour.instance()!!.armourHacker.activityThread }

    companion object {
        val COMPONENT = "COMPONENT"
        val MODULE_NAME = "MODULE_NAME"
        val ARG_OP = "ARG_OP"
        val START = "START"
        val STOP = "STOP"
        val BIND = "BIND"
        val UNBIND = "UNBIND"

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
    val bindedService = mutableSetOf<Service>()

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        println("ArmourService onStartCommand")
        if (intent == null) return super.onStartCommand(intent, flags, startId)

        val module = intent.getStringExtra(MODULE_NAME) ?: return super.onStartCommand(intent, flags, startId)
        val component = intent.getStringExtra(COMPONENT) ?: return super.onStartCommand(intent, flags, startId)
        val operation = intent.getStringExtra(ARG_OP)
        println("ArmourService onStartCommand $component")

        var service = map[component]
        if (service == null) {
            when (operation) {
                STOP, UNBIND -> return super.onStartCommand(intent, flags, startId)
            }

            val aPlugin = Armour.instance(this.application).getPlugin(module) ?: return super.onStartCommand(intent, flags, startId)
            service = aPlugin.aPluginClassloader.loadClass(component).newInstance() as Service

            try {
                Hacker.on(service::class.java)
                        .method("attach", Context::class.java, Class.forName("android.app.ActivityThread"),
                                String::class.java, IBinder::class.java,
                                Application::class.java, Any::class.java) // Any::class.java == Objct
                        ?.invoke(service, aPlugin.aPluginContext, mThread, mClassName, mToken, application, mActivityManager)
            } catch (e: Exception) {
                e.printStackTrace()
                return super.onStartCommand(intent, flags, startId)
            }

            service.onCreate()
            map[component] = service
        }

        when (operation) {
            START -> service.onStartCommand(intent, flags, startId)
            STOP -> {
                // 这里不判断是否bind
                service.onDestroy()
                removeServiceConn(component)
                map.remove(component)
            }
            BIND -> {
                if (bindedService.contains(service)) return super.onStartCommand(intent, flags, startId)
                bindedService.add(service)

                val binder = service.onBind(intent)
                val conn = getServiceConn(component) ?: return super.onStartCommand(intent, flags, startId)
                val pn = parseClassPackage(component)
                conn.onServiceConnected(ComponentName(pn, component), binder)
            }
            UNBIND -> {
                service.onUnbind(intent) // todo change real intent

                bindedService.remove(service)
                removeServiceConn(component)
                map.remove(component)
            }
        }
        return super.onStartCommand(intent, flags, startId)
    }
}