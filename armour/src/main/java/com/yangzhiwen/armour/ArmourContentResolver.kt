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
        println("ArmourContentResolver acquireProvider $name")
        Navigator.instance.readComponentToComponent.filter {
            it.value is ProviderComponent
        }.forEach {
            val component = it.value as ProviderComponent
            if (component.url.toString() == name) {
                println(component.realComponent)
            }
            if (component.isPlugin) return Armour.instance()?.armourIContentProvider as IContentProvider?
        }

        val base = Armour.instance()?.application?.contentResolver ?: return null
        val icp = Hacker.on(base.javaClass)
                .method("acquireProvider", Context::class.java, String::class.java)
                ?.invoke(base, c, name) as IContentProvider
        return icp
    }


    fun acquireUnstableProvider(context: Context, auth: String): IContentProvider? {
        println("ArmourContentResolver acquireUnstableProvider $auth")
        Navigator.instance.readComponentToComponent.filter {
            it.value is ProviderComponent
        }.forEach {
            val component = it.value as ProviderComponent
            if (component.url.toString() == auth) {
                println(component.realComponent)
            }
            if (component.isPlugin) return Armour.instance()?.armourIContentProvider as IContentProvider?
        }

        val base = Armour.instance()?.application?.contentResolver ?: return null
        val icp = Hacker.on(base.javaClass)
                .method("acquireUnstableProvider", Context::class.java, String::class.java)
                ?.invoke(base, context, auth) as IContentProvider
        return icp
    }


    fun releaseProvider(provider: IContentProvider): Boolean {
        return true
    }

    fun releaseUnstableProvider(icp: IContentProvider): Boolean {
        return true
    }

    fun unstableProviderDied(icp: IContentProvider) {}

    fun appNotRespondingViaProvider(icp: IContentProvider) {}

    /** @hide
     */
    protected fun resolveUserIdFromAuthority(auth: String): Int {
        return 0
    }
}