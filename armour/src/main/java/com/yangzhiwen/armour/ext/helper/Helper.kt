package com.yangzhiwen.armour.ext.helper

import android.net.Uri
import com.yangzhiwen.armour.proxy.ArmourContentProvider
import com.yangzhiwen.armour.compass.NavigatorComponent

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


fun wrapUrl(component: NavigatorComponent, url: Uri): Uri {
    val oUrl = url.toString()
    val newUrl = "${ArmourContentProvider.URI}/?${ArmourContentProvider.MODULE}=${component.module}&${ArmourContentProvider.COMPONENT}=${component.name}&${ArmourContentProvider.PLUGIN_URI}=$oUrl"
    return Uri.parse(newUrl)
}