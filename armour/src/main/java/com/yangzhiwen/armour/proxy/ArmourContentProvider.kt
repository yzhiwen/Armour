package com.yangzhiwen.armour.proxy

import android.content.ComponentName
import android.content.ContentProvider
import android.content.ContentValues
import android.content.pm.ProviderInfo
import android.database.Cursor
import android.net.Uri
import com.yangzhiwen.armour.Armour
import com.yangzhiwen.armour.compass.Navigator

class ArmourContentProvider : ContentProvider() {

    val acpInfo: ProviderInfo by lazy {
        Armour.instance()!!.application.packageManager.getProviderInfo(ComponentName(context, this::class.java), 0)
    }

    companion object {
        val AUTHORITY = "com.yangzhiwen.armour"
        val URI = Uri.parse("content://com.yangzhiwen.armour")
        val PLUGIN_URI = "PLUGIN_URI"
        val MODULE = "MODULE"
        val COMPONENT = "COMPONENT"
    }

    override fun onCreate(): Boolean {
        println("ArmourContentProvider onCreate")
        return true
    }

    override fun getType(uri: Uri): String? {
        println("ArmourContentProvider getType")
        return null
    }

    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<String>?): Int {
        println("ArmourContentProvider delete $uri")
        return getPluginProvider(uri)?.delete(getPluginUri(uri), selection, selectionArgs) ?: return 0
    }

    override fun insert(uri: Uri, values: ContentValues?): Uri? {
        println("ArmourContentProvider insert $uri")
        return getPluginProvider(uri)?.insert(getPluginUri(uri), values) ?: return null
    }

    override fun query(uri: Uri, projection: Array<String>?, selection: String?,
                       selectionArgs: Array<String>?, sortOrder: String?): Cursor? {
        println("ArmourContentProvider query $uri")
        return getPluginProvider(uri)?.query(getPluginUri(uri), projection, selection, selectionArgs, sortOrder)
    }

    override fun update(uri: Uri, values: ContentValues?, selection: String?,
                        selectionArgs: Array<String>?): Int {
        println("ArmourContentProvider update $uri")
        return getPluginProvider(uri)?.update(getPluginUri(uri), values, selection, selectionArgs) ?: return 0
    }

    val map = mutableMapOf<String, ContentProvider>()

    fun getPluginUri(uri: Uri): Uri {
        val pluginUri = uri.getQueryParameter(PLUGIN_URI)
        return Uri.parse(pluginUri)
    }

    fun getPluginProvider(uri: Uri): ContentProvider? {
        val module = uri.getQueryParameter(MODULE)
        val name = uri.getQueryParameter(COMPONENT)
        val uri = uri.getQueryParameter(PLUGIN_URI)

        println("getPluginProvider || $module :: $name :: $uri")
        if (module == null || name == null || uri == null) return null

        val key = "$module-$name"
        if (map[key] != null) return map[key]

        val armour = Armour.instance() ?: return null
        val realComponent = Navigator.instance.getModule(module)?.getComponent(name)?.realComponent ?: return null
        val aPlugin = armour.getPlugin(module) ?: return null
        val cp = aPlugin.aPluginClassloader.loadClass(realComponent)?.newInstance() as ContentProvider
        //  provider context resource and call onCreate
        cp.attachInfo(aPlugin.aPluginContext, acpInfo)
        map[key] = cp
        return cp
    }
}
