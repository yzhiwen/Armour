package com.yangzhiwen.armour.ext

import android.net.Uri
import com.yangzhiwen.armour.proxy.ArmourContentProvider
import com.yangzhiwen.armour.compass.NavigatorComponent

/**
 * Created by yangzhiwen on 17/8/16.
 */

// todo unit test
fun parseClassPackage(className: String): String {
    val index = className.lastIndexOf("")
    if (index == -1) return ""
    else return className.subSequence(0, index).toString()
//  val name = className.subSequence(index, className.length).toString()
}


fun wrapUrl(component: NavigatorComponent, url: Uri): Uri {
    val oUrl = url.toString()
    val newUrl = "${ArmourContentProvider.URI}/?${ArmourContentProvider.MODULE}=${component.module}&${ArmourContentProvider.COMPONENT}=${component.name}&${ArmourContentProvider.PLUGIN_URI}=$oUrl"
    return Uri.parse(newUrl)
}