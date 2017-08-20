package com.yangzhiwen.armour

import android.content.ContentResolver
import android.content.Context
import android.content.IContentProvider
import com.yangzhiwen.armour.compass.Navigator
import com.yangzhiwen.armour.ext.Hacker
import com.yangzhiwen.armour.ext.compass.ProviderComponent

/**
 * Created by yangzhiwen on 17/8/18.
 */
class ArmourContentResolver(context: Context, val armour: Armour) : ContentResolver(context) {

    fun acquireProvider(c: Context, auth: String): IContentProvider? {
        println("ArmourContentResolver acquireProvider $auth")
        // todo flat map
        Navigator.instance.readComponentToComponent.filter {
            it.value is ProviderComponent
        }.forEach {
            val component = it.value as ProviderComponent
            if (component.url.toString() == "content://$auth" && component.isPlugin) {
                println(component.realComponent)
                return armour.armourIContentProvider as IContentProvider?
            }
        }

        return acquireBaseProvider(c,auth,"acquireProvider")
    }

    fun acquireUnstableProvider(context: Context, auth: String): IContentProvider? {
        println("ArmourContentResolver acquireUnstableProvider $auth")
        // todo flat map
        Navigator.instance.readComponentToComponent.filter {
            it.value is ProviderComponent
        }.forEach {
            val component = it.value as ProviderComponent
            if (component.url.toString() == "content://$auth" && component.isPlugin) {
                println(component.realComponent)
                return armour.armourIContentProvider as IContentProvider?
            }
        }

        return acquireBaseProvider(context,auth,"acquireUnstableProvider")
    }

    fun acquireBaseProvider(context: Context, auth: String, methodName: String): IContentProvider? {

        val base = armour.application.contentResolver ?: return null
        val icp = Hacker.on(base.javaClass)
                .declaredMethod(methodName, Context::class.java, String::class.java)
                ?.invoke(base, context, auth)
        return icp as IContentProvider?
    }

    fun releaseProvider(provider: IContentProvider) = true

    fun releaseUnstableProvider(icp: IContentProvider) = true

    fun unstableProviderDied(icp: IContentProvider) {}

    fun appNotRespondingViaProvider(icp: IContentProvider) {}

    fun resolveUserIdFromAuthority(auth: String) = 0
}