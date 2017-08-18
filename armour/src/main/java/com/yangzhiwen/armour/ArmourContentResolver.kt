package com.yangzhiwen.armour

import android.content.ContentResolver
import android.content.Context
import android.content.IContentProvider
import com.yangzhiwen.armour.compass.Navigator
import com.yangzhiwen.armour.ext.compass.ProviderComponent

/**
 * Created by yangzhiwen on 17/8/18.
 */
class ArmourContentResolver(context: Context) : ContentResolver(context) {

    // todo
    fun acquireProvider(c: Context, name: String): IContentProvider? {
        println("ArmourContentResolver $name")
        Navigator.instance.readComponentToComponent.filter {
            it.value is ProviderComponent
        }.forEach {
            val component = it.value as ProviderComponent
            if (component.url.toString() == name) {
                println(component.realComponent)
            }
        }
        return null
    }
}