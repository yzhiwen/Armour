package com.yangzhiwen.navigator


import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import com.yangzhiwen.armour.compass.Navigator
import com.yangzhiwen.armour.ext.compass.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        App.res = resources

        //  todo
//        val layout = LinearLayout(this)
//        println(layout.layoutParams)  // null
//        println(layout.layoutParams.width) // java.lang.NullPointerException: Attempt to read from field 'int android.view.ViewGroup$LayoutParams.width' on a null object reference

        Navigator.instance.context = this
        findViewById<Button>(R.id.btn).setOnClickListener {
            Navigator.instance.startActivity("host", "other", "aaa")
        }

        findViewById<Button>(R.id.user).setOnClickListener {
            Navigator.instance.startActivity("user_center", "center", "arg")
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
    }
}