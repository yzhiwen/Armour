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


        // todo 自动化 & 动态化
        // 宿主路由
        Navigator.instance.register("host", false) {
            registerActivityComponent("pay", "com.yangzhiwen.navigator.MainActivity")
            registerActivityComponent("other", "com.yangzhiwen.navigator.OtherActivity")
        }


        // 插件 路由
        Navigator.instance.register("user_center", true) {
            registerActivityComponent("main", "com.yangzhiwen.demo.MainActivity")
            registerActivityComponent("center", "com.yangzhiwen.demo.CenterActivity")

            registerServiceComponent("user_service", "com.yangzhiwen.demo.UserCenterService", false)
            registerServiceComponent("user_remote_service", "com.yangzhiwen.demo.UserRemoteService")

            val actions = arrayOf("user_center_msg", "user_center_setting_msg")
            registerReceiverComponent("user_center_receiver", "com.yangzhiwen.demo.UserCenterReceiver", *actions)

            registerProviderComponent("user_provider", "com.yangzhiwen.demo.UserContentProvider",
                    Uri.parse("content://com.yangzhiwen.user"))
        }


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