package com.yangzhiwen.demo

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

/**
 * Created by yangzhiwen on 17/8/14.
 */
class UserCenterReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        println(" ")
        println("====================================")
        println("user center on receive $intent")
        println("====================================")
        println(" ")
    }
}