package com.yangzhiwen.navigator


import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.LinearLayout
import com.yangzhiwen.armour.APlugin
import com.yangzhiwen.compass.Navigator

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //  todo
//        val layout = LinearLayout(this)
//        println(layout.layoutParams)  // null
//        println(layout.layoutParams.width) // java.lang.NullPointerException: Attempt to read from field 'int android.view.ViewGroup$LayoutParams.width' on a null object reference

        Navigator.instance.context = this
        findViewById<Button>(R.id.btn).setOnClickListener {
            Navigator.instance.nav("host", "other", "aaa")
        }

        findViewById<Button>(R.id.user).setOnClickListener {
            Navigator.instance.nav("user_center", "center", "arg")
        }

        findViewById<Button>(R.id.user2).setOnClickListener {
            Navigator.instance.nav("user_center", "center", "arg2")
        }

        findViewById<Button>(R.id.send).setOnClickListener {
            sendBroadcast(Intent("user_center_msg"))
        }

    }
}


