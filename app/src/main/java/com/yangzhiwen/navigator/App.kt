package com.yangzhiwen.navigator

import android.app.Application
import android.os.Environment
import com.yangzhiwen.armour.APlugin
import com.yangzhiwen.armour.Armour
import com.yangzhiwen.compass.Navigator
import com.yangzhiwen.compass.ext.registerActivityComponent
import kotlin.concurrent.thread
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import dalvik.system.DexClassLoader


/**
 * Created by yangzhiwen on 2017/8/13.
 */
class App : Application() {

    companion object {
        var instance: App? = null
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
        Tester().run()

        Armour.instance(this)
        thread {
            val filePath = "sdcard/main.apk"
            val outPath = filesDir.absolutePath + "/patch/out.apk"

            if (!File(outPath).exists()) {
                File(outPath).parentFile.mkdirs()
                File(outPath).createNewFile()
            }

            val fi = FileInputStream(File(filePath))
            val fo = FileOutputStream(File(outPath), false)

            val buffer = ByteArray(4096)
            while (true) {
                val len = fi.read(buffer)
                if (len <= 0) break
                fo.write(buffer, 0, len)
            }

            fo.flush()
            fo.close()
            fi.close()

            Armour.instance(this).instantPlugin("user_center", outPath)

            Navigator.instance.registerActivityComponent("user_center", "setting", "com.yangzhiwen.newdemo.MainActivity")
            Navigator.instance.registerActivityComponent("user_center", "center", "com.yangzhiwen.newdemo.CenterActivity")
        }


    }
}