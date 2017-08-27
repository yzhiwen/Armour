package com.yangzhiwen.navigator

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Button

class OtherActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_other)

        findViewById<Button>(R.id.btn).setOnClickListener {
            startActivity(Intent(this,MainActivity::class.java))
        }
    }
}
