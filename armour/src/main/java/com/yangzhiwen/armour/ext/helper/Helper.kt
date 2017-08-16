package com.yangzhiwen.armour.ext.helper

/**
 * Created by yangzhiwen on 17/8/16.
 */

// todo 测试用例
fun parseClassName(className: String): Pair<String, String> {
    val index = className.lastIndexOf(".")
    if (index == -1) return Pair("", className)
    else {
        val packageName = className.subSequence(0, index).toString()
        val name = className.subSequence(index, className.length).toString()
        return Pair(packageName, name)
    }
}
