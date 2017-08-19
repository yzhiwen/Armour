package com.yangzhiwen.navigator

import android.app.Application
import android.net.Uri
import com.yangzhiwen.armour.Armour
import com.yangzhiwen.armour.compass.Navigator
import com.yangzhiwen.armour.ext.compass.*
import kotlin.concurrent.thread
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream


/**
 * Created by yangzhiwen on 2017/8/13.
 */
class App : Application() {

    override fun onCreate() {
        super.onCreate()

        Armour.instance(this)

        Navigator.instance.context = this
        // todo 自动化 & 动态化
        // 宿主路由
        Navigator.instance.registerActivityComponent(false, "host", "pay", "com.yangzhiwen.navigator.MainActivity")
        Navigator.instance.registerActivityComponent(false, "host", "other", "com.yangzhiwen.navigator.OtherActivity")

        // 插件 路由
        Navigator.instance.registerActivityComponent(true, "user_center", "main", "com.yangzhiwen.demo.MainActivity")
        Navigator.instance.registerActivityComponent(true, "user_center", "center", "com.yangzhiwen.demo.CenterActivity")
        Navigator.instance.registerServiceComponent(true, "user_center", "user_service", "com.yangzhiwen.demo.UserCenterService", false)
        Navigator.instance.registerServiceComponent(true, "user_center", "user_service", "com.yangzhiwen.demo.UserRemoteService", true)

        val actions = arrayOf("user_center_msg", "user_center_setting_msg")
        Navigator.instance.registerReceiverComponent(true, "user_center", "user_center_receiver", "com.yangzhiwen.demo.UserCenterReceiver", *actions)

        Navigator.instance.registerProviderComponent(true, "user_center", "user_provider", "com.yangzhiwen.demo.UserContentProvider", Uri.parse("com.yangzhiwen.user"))

        thread {
            val outPath = copy()
            Armour.instance(this).instantPlugin("user_center", outPath)
        }
    }

    private fun copy(): String {
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
        return outPath
    }
}