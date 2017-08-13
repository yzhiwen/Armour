package com.yangzhiwen.navigator

import com.yangzhiwen.compass.Navigator
import com.yangzhiwen.compass.ext.registerActivityComponent
import com.yangzhiwen.compass.ext.registerActivityComponentHandler

/**
 * Created by yangzhiwen on 2017/8/13.
 */
class Tester {
    fun run() {
        Navigator.instance.registerActivityComponent("host", "pay", "com.yangzhiwen.navigator.MainActivity")
        Navigator.instance.registerActivityComponent("host", "other", "com.yangzhiwen.navigator.OtherActivity")
        Navigator.instance.registerActivityComponentHandler()
    }
}