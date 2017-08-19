package com.yangzhiwen.navigator

import android.app.Application
import android.content.ContentProvider
import android.content.Intent
import android.content.pm.ProviderInfo
import android.net.Uri
import android.os.Build
import android.support.annotation.RequiresApi
import com.yangzhiwen.armour.Armour
import com.yangzhiwen.armour.ArmourIContentProvider
import com.yangzhiwen.armour.Hacker
import com.yangzhiwen.armour.compass.Navigator
import com.yangzhiwen.armour.ext.compass.*
import com.yangzhiwen.armour.proxy.ArmourContentProvider
import kotlin.concurrent.thread
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.lang.reflect.Proxy


/**
 * Created by yangzhiwen on 2017/8/13.
 */
class App : Application() {

    @RequiresApi(Build.VERSION_CODES.KITKAT)
    override fun onCreate() {
        super.onCreate()

//        test()
//        test2()

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

//        Navigator.instance.registerActivityComponentHandler()
//        Navigator.instance.registerServiceComponentHandler()
//        Navigator.instance.registerProviderComponentHandler()

        thread {
            val outPath = copy()
            Armour.instance(this).instantPlugin("user_center", outPath)
            test2()
        }
    }

    private fun test2() {
//        try {
//            contentResolver.insert(Uri.parse("content://com.yangzhiwen.demo"), null)
//        } catch (e: Exception) {
//            e.printStackTrace()
//        }

        val intent = Intent(this, ArmourContentProvider::class.java)
        val ap = packageManager.getProviderInfo(intent.component, 0)
        println(" === || $ap")

        val c = ProviderInfo()
        c.applicationInfo = this.applicationInfo
        c.authority = "com.yangzhiwen.demo"


        val a = "android.app.IActivityManager${'$'}ContentProviderHolder"
        val b = Class.forName(a).getConstructor(ProviderInfo::class.java).newInstance(c)
        println(b)

        val activityThread = Hacker.on(baseContext.javaClass)
                .field("mMainThread")
                ?.get(baseContext) ?: return

        val mProviderMap = Hacker.on(activityThread.javaClass)
                .field("mProviderMap")
                ?.get(activityThread) as Map<*, *>

        println(" =========== size : ${mProviderMap.size}")

        var icp: Any? = null

        for ((k, v) in mProviderMap) {
            if (k == null || v == null) continue
            val authority = Hacker.on(k.javaClass)
                    .field("authority")
                    ?.get(k) as String ?: continue
            println(authority)
            if (authority == "com.yangzhiwen.armour") {
                icp = Hacker.on(v.javaClass)
                        .field("mProvider")
                        ?.get(v)
                break
            }
        }
        val cl = arrayOf(Class.forName("android.content.IContentProvider"))
        val proxy = Proxy.newProxyInstance(classLoader, cl, ArmourIContentProvider(icp))

//        public static IContentProvider newInstance(Context context, IContentProvider iContentProvider) {
//            return (IContentProvider) Proxy.newProxyInstance(iContentProvider.getClass().getClassLoader(),
//            new Class[] { IContentProvider.class }, new IContentProviderProxy(context, iContentProvider));
//        }

        println("======")
        println(icp)
        println("======")


        val d = Class.forName("android.content.IContentProvider")
        val e = ContentProvider::class.java
        val f = Class.forName(a)

        val aPluginClassloader = Armour.instance(this).getPlugin("user_center")?.aPluginClassloader ?: return
        val cp = aPluginClassloader.loadClass("com.yangzhiwen.demo.UserContentProvider").newInstance()
        Hacker.on(activityThread.javaClass)
                .method("installProviderAuthoritiesLocked", d, e, f)
                ?.invoke(activityThread, proxy, cp, b)

        val newMap = Hacker.on(activityThread.javaClass)
                .field("mProviderMap")
                ?.get(activityThread) as Map<*, *>

        println(" =========== size : ${newMap.size}")

        try {
//            contentResolver.insert(Uri.parse("content://com.yangzhiwen.demo"), null)
            contentResolver.delete(Uri.parse("content://com.yangzhiwen.demo"), null, null)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun test() {
        val res = Hacker.on(contentResolver::class.java)
                .method("resolveUserIdFromAuthority", String::class.java)
                ?.invoke(contentResolver, "content://com.yangzhiwen") as Int
        print("res $res")

        val demo = Hacker.on(contentResolver::class.java)
                .method("resolveUserIdFromAuthority", String::class.java)
                ?.invoke(contentResolver, "content://com.yangzhiwen.demo") as Int
        print("res $demo")


        val activityThread = Hacker.on(baseContext.javaClass)
                .field("mMainThread")
                ?.get(baseContext)

        if (activityThread != null) {
            val mProviderMap = Hacker.on(activityThread.javaClass)
                    .field("mProviderMap")
                    ?.get(activityThread) as Map<*, *>

            println(" =========== size : ${mProviderMap.size}")
            for ((k, v) in mProviderMap) {
                if (k != null)
                    println(k.javaClass.name)
                if (v != null)
                    println(v.javaClass.name)
            }

            val key = "android.app.ActivityThread${'$'}ProviderKey"
            val keyInstanceC = Class.forName(key).getConstructor(String::class.java, Int::class.java)
            keyInstanceC.isAccessible = true
            val keyInstance = keyInstanceC.newInstance("content://com.yangzhiwen.demo", 0)
            println("$keyInstance")

            val value = "android.app.ActivityThread${'$'}ProviderClientRecord"
            val a = emptyArray<String>()::class.java
            val b = Class.forName("android.content.IContentProvider")
            val c = Class.forName("android.app.IActivityManager${'$'}ContentProviderHolder")
            val d = ContentProvider::class.java
            //                val valueClass = Class.forName(value).getDeclaredConstructor(activityThread::class.java, a, b, c, d)
            val valueClass = Class.forName(value).declaredConstructors
            if (valueClass.isNotEmpty()) {
                valueClass[0].isAccessible = true
                //                valueClass.isAccessible = true
                val str = arrayOf("yzw", "dazz")
                // 五个参数因为是外部类是ActivityThread
                val valueInstance = valueClass[0].newInstance(activityThread, str, null, null, null)
                println(valueInstance)

                //                Hacker.on(mProviderMap)

                Hacker.on(Map::class.java)
                        .method("put", Any::class.java, Any::class.java)
                        ?.invoke(keyInstance, valueInstance)


                println(" =========== size : ${mProviderMap.size}")
            }


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