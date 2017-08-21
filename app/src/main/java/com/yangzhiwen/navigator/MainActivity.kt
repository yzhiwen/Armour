package com.yangzhiwen.navigator

import android.app.Activity
import android.os.Bundle
import android.widget.Button
import com.yangzhiwen.armour.Armour
import com.yangzhiwen.armour.proxy.ArmourActivity
import java.io.File
import java.io.FileOutputStream
import kotlin.concurrent.thread

class MainActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //  note
//        val layout = LinearLayout(this)
//        println(layout.layoutParams)  // null
//        println(layout.layoutParams.width) // java.lang.NullPointerException: Attempt to read from field 'int android.view.ViewGroup$LayoutParams.width' on a null object reference

        println(ArmourActivity::class.java)
        findViewById<Button>(R.id.btn).setOnClickListener {
            // todo async
            thread {
                val outPath = download()
                Armour.instance()?.instantPlugin("user_center", outPath)
            }
        }

        findViewById<Button>(R.id.main).setOnClickListener {
            Armour.instance(application).getPlugin("user_center")?.start()
        }

    }


    private fun download(): String {
        val outPath = filesDir.absolutePath + "/patch/plugin.apk"

        if (!File(outPath).exists()) {
            File(outPath).parentFile.mkdirs()
            File(outPath).createNewFile()
        }

        val fi = assets.open("plugin.apk")
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