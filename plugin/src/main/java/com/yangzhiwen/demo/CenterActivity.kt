package com.yangzhiwen.demo

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView

// todo extend AppCompatActivity has some resource error
// 有堆栈信息，主要是v7包的错
class CenterActivity : Activity() {

    override fun attachBaseContext(newBase: Context?) {
        super.attachBaseContext(newBase)

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

//        codeView()
// todo 如果没有处理资源的话，通过欺上瞒下启动该Activity会失败，不会报错，而是跳转到Proxy Activity
        xmlView()
    }

    private fun xmlView() {
        setContentView(R.layout.activity_setting)
        findViewById<Button>(R.id.send).setOnClickListener {
            sendBroadcast(Intent("user_center_setting_msg"))
        }
    }

    private fun codeView() {
        val layout = LinearLayout(this)
        //        layout.layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT
        //        layout.layoutParams.height = ViewGroup.LayoutParams.MATCH_PARENT
        layout.orientation = LinearLayout.VERTICAL


        val tv = TextView(this)
        tv.text = "user center activity"

        val btn = Button(this)
        btn.text = "send msg to user center receiver"
        btn.setOnClickListener {
            sendBroadcast(Intent("user_center_setting_msg"))
        }
        layout.addView(tv)
        layout.addView(btn)

        setContentView(layout)
    }
}
