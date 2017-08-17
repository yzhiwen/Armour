package com.yangzhiwen.armour

import android.app.Activity
import android.app.Instrumentation
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.IBinder

/**
 * Created by yangzhiwen on 2017/8/18.
 */
class ArmourInstrumentation(baseArg: Instrumentation, armourArg: Armour) : Instrumentation() {


    fun execStartActivity(
            who: Context?, contextThread: IBinder?, token: IBinder?, target: Activity?,
            intent: Intent?, requestCode: Int?, options: Bundle?): ActivityResult {

        return
    }
}