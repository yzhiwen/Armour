package com.yangzhiwen.navigator

import android.app.Application
import com.yangzhiwen.armour.Armour
import kotlin.concurrent.thread
import java.io.File
import java.io.FileOutputStream


/**
 * Created by yangzhiwen on 2017/8/13.
 */
class App : Application() {

    override fun onCreate() {
        super.onCreate()

        Armour.instance(this)

    }

}