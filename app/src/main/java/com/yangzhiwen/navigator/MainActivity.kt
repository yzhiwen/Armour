package com.yangzhiwen.navigator


import android.content.ComponentName
import android.content.ContentValues
import android.content.Intent
import android.content.ServiceConnection
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.IBinder
import android.widget.Button
import com.yangzhiwen.armour.compass.Navigator
import com.yangzhiwen.armour.ext.compass.*
import com.yangzhiwen.armour.proxy.ArmourActivity

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //  todo
//        val layout = LinearLayout(this)
//        println(layout.layoutParams)  // null
//        println(layout.layoutParams.width) // java.lang.NullPointerException: Attempt to read from field 'int android.view.ViewGroup$LayoutParams.width' on a null object reference

        println(ArmourActivity::class.java)
        findViewById<Button>(R.id.btn).setOnClickListener {
            Navigator.instance.startActivity("host", "other", "aaa")
        }

        findViewById<Button>(R.id.main).setOnClickListener {
            Navigator.instance.startActivity("user_center", "main", "arg")
        }

        findViewById<Button>(R.id.user2).setOnClickListener {
            Navigator.instance.startActivity("user_center", "center", "arg2")
        }

        findViewById<Button>(R.id.send).setOnClickListener {
            Navigator.instance.sendBroadcast(Intent("user_center_msg"))
        }

        findViewById<Button>(R.id.start_local_service).setOnClickListener {
            Navigator.instance.startService("user_center", "user_service", "user_service arg xixi")
        }

        findViewById<Button>(R.id.stop_local_service).setOnClickListener {
            Navigator.instance.stopService("user_center", "user_service", "user_service arg xixi")
        }

        findViewById<Button>(R.id.bind_local_service).setOnClickListener {
            Navigator.instance.bindService("user_center", "user_service", SC(), "user_service arg xixi")
        }

        findViewById<Button>(R.id.insert).setOnClickListener {
            val cv = ContentValues()
            Navigator.instance.insert("user_center", "user_provider", Uri.parse("com.yangzhiwen.user"), cv)
        }

        findViewById<Button>(R.id.delete).setOnClickListener {
            Navigator.instance.delete("user_center", "user_provider", Uri.parse("com.yangzhiwen.user"), null, null)
        }

        findViewById<Button>(R.id.query).setOnClickListener {
            Navigator.instance.query("user_center", "user_provider", Uri.parse("com.yangzhiwen.user"), null, null, null, "") {
                cursor ->
                println(cursor)
            }
        }
    }


    class SC : ServiceConnection {
        override fun onServiceDisconnected(p0: ComponentName?) {

        }

        override fun onServiceConnected(p0: ComponentName?, p1: IBinder?) {
            println("========== onServiceConnected")
        }
    }
}