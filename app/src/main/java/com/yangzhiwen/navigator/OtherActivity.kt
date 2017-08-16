package com.yangzhiwen.navigator

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import com.yangzhiwen.armour.compass.Navigator
import com.yangzhiwen.armour.ext.compass.startActivity

class OtherActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_other)

        findViewById<Button>(R.id.btn).setOnClickListener {
            Navigator.instance.startActivity("host", "pay", "aaa")
        }

    }
}
