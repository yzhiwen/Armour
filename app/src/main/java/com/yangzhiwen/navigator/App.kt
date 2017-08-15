package com.yangzhiwen.navigator

import android.app.Application
import android.content.BroadcastReceiver
import android.content.IntentFilter
import android.content.res.Resources
import com.yangzhiwen.armour.ActivityLifecycleListener
import com.yangzhiwen.armour.Armour
import com.yangzhiwen.compass.Navigator
import com.yangzhiwen.navigator.ext.armour.ArmourClassLoaderInterceptorImpl
import com.yangzhiwen.navigator.ext.navigator.*
import com.yangzhiwen.navigator.other.ArmourInstrumentation
import com.yangzhiwen.navigator.other.ReflectUtil
import kotlin.concurrent.thread
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream


/**
 * Created by yangzhiwen on 2017/8/13.
 */
class App : Application() {

    companion object {
        var instance: App? = null
        var path: String? = null
        var res: Resources? = null
    }

    override fun onCreate() {
        super.onCreate()
        instance = this

        registerActivityLifecycleCallbacks(ActivityLifecycleListener.instance)

        // todo 自动化 & 动态化
        Navigator.instance.registerActivityComponent(false, "host", "pay", "com.yangzhiwen.navigator.MainActivity")
        Navigator.instance.registerActivityComponent(false, "host", "other", "com.yangzhiwen.navigator.OtherActivity")
        Navigator.instance.registerActivityComponent(true, "user_center", "setting", "com.yangzhiwen.demo.MainActivity")
        Navigator.instance.registerActivityComponent(true, "user_center", "center", "com.yangzhiwen.demo.CenterActivity")
        Navigator.instance.registerServiceComponent(true, "user_center", "user_service", "com.yangzhiwen.demo.UserCenterService")
        val actions = arrayOf("user_center_msg", "user_center_setting_msg")
        Navigator.instance.registerReceiverComponent(true, "user_center", "user_center_receiver", "com.yangzhiwen.demo.UserCenterReceiver", *actions)

        Navigator.instance.registerActivityComponentHandler()
        Navigator.instance.registerServiceComponentHandler()

        Armour.instance(this)
        Armour.instance(this).classLoaderInterceptor = ArmourClassLoaderInterceptorImpl()


        thread {
            val outPath = copy()
            path = outPath

            val plugin = Armour.instance(this).instantPlugin("user_center", outPath)

            // 加载路由的信息
            val recevier = Armour.instance()?.getPlugin("user_center")?.classloader?.loadClass("com.yangzhiwen.demo.UserCenterReceiver")?.newInstance() as BroadcastReceiver
            val filter = IntentFilter()
            for (action in actions) {
                filter.addAction(action)
            }
            registerReceiver(recevier, filter)
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