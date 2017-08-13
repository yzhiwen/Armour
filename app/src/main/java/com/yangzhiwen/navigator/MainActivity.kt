package com.yangzhiwen.navigator


import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import com.yangzhiwen.armour.APlugin
import com.yangzhiwen.compass.Navigator

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


//

        Navigator.instance.context = this
        findViewById<Button>(R.id.btn).setOnClickListener {
            Navigator.instance.nav("host", "other", "aaa")
        }

        findViewById<Button>(R.id.user).setOnClickListener {
            Navigator.instance.nav("user_center", "center", "arg")
        }
    }
}


