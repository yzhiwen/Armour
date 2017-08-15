package com.yangzhiwen.demo

import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.IBinder

/**
 * Created by yangzhiwen on 17/8/15.
 */
class UserCenterService : Service() {

    override fun onBind(p0: Intent?): IBinder? {
        println(" ")
        println("====================================")
        println("user center service on bind")
        println("====================================")
        println(" ")
        return null
    }

    override fun attachBaseContext(base: Context?) {
        println(" ")
        println("====================================")
        println("user center service on attach base context")
        println("====================================")
        println(" ")
        super.attachBaseContext(base)
    }

    override fun onCreate() {
        println(" ")
        println("====================================")
        println("user center service on create")
        println("====================================")
        println(" ")
        super.onCreate()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        println(" ")
        println("====================================")
        println("user center service on start command $intent")
        println("====================================")
        println(" ")
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onDestroy() {
        println(" ")
        println("====================================")
        println("user center service on destroy")
        println("====================================")
        println(" ")
        super.onDestroy()
    }

    override fun onUnbind(intent: Intent?): Boolean {
        println(" ")
        println("====================================")
        println("user center service on un bind")
        println("====================================")
        println(" ")
        return super.onUnbind(intent)
    }
}