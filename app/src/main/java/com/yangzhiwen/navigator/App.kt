package com.yangzhiwen.navigator

import android.app.Application
import android.content.BroadcastReceiver
import android.content.IntentFilter
import android.content.res.Resources
import com.yangzhiwen.armour.Armour
import com.yangzhiwen.compass.Navigator
import com.yangzhiwen.navigator.ext.armour.ArmourClassLoaderInterceptorImpl
import com.yangzhiwen.navigator.ext.navigator.*
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

        Navigator.instance.registerActivityComponent("host", "pay", "com.yangzhiwen.navigator.MainActivity")
        Navigator.instance.registerActivityComponent("host", "other", "com.yangzhiwen.navigator.OtherActivity")
        Navigator.instance.registerActivityComponent("user_center", "setting", "com.yangzhiwen.demo.MainActivity")
        Navigator.instance.registerActivityComponent("user_center", "center", "com.yangzhiwen.demo.CenterActivity")
        Navigator.instance.registerServiceComponent("user_center","user_service","com.yangzhiwen.demo.UserCenterService")
        Navigator.instance.registerActivityComponentHandler()
        Navigator.instance.registerServiceComponentHandler()


        Armour.instance(this)
        Armour.instance(this).classLoaderInterceptor = ArmourClassLoaderInterceptorImpl()


        // 插件 组件  关联 宿主 组件
        Armour.instance(this).classLoaderInterceptor?.classMapToModule?.put("com.yangzhiwen.navigator.ProxyActivity", "user_center")
        Armour.instance(this).classLoaderInterceptor?.realClassMap?.put("com.yangzhiwen.navigator.ProxyActivity", "com.yangzhiwen.demo.CenterActivity")

        thread {
            val outPath = copy()
            path = outPath

            val plugin = Armour.instance(this).instantPlugin("user_center", outPath)



            val actions = arrayOf("user_center_msg", "user_center_setting_msg")
            Navigator.instance.registerReceiverComponent(this, "user_center", "user_center_receiver", "com.yangzhiwen.demo.UserCenterReceiver", *actions)

//            plugin?.classloader?.loadClass("com.yangzhiwen.demo.UserCenterReceiver")
            // 加载路由的信息
            val recevier = Armour.instance()?.getPlugin("user_center")?.classloader?.loadClass("com.yangzhiwen.demo.UserCenterReceiver")?.newInstance() as BroadcastReceiver
            val filter = IntentFilter()
            for (action in actions) {
                filter.addAction(action)
            }
            registerReceiver(recevier, filter)
        }
    }

    private fun hookInstrumentationAndHandler() {
        val baseInstrumentation = ReflectUtil.getInstrumentation(this)
        val instrumentation = ArmourInstrumentation(baseInstrumentation, Armour.instance(this))
        val activityThread = ReflectUtil.getActivityThread(this)
        ReflectUtil.setInstrumentation(activityThread, instrumentation)
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