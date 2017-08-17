package com.yangzhiwen.armour

import android.content.ContentProvider
import android.content.ContentValues
import android.database.Cursor
import android.net.Uri
import com.yangzhiwen.armour.compass.Navigator

class ArmourContentProvider : ContentProvider() {

    companion object {
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
        return getPluginProvider(uri)?.delete(uri, selection, selectionArgs) ?: return 0
    }

    override fun insert(uri: Uri, values: ContentValues?): Uri? {
        println("ArmourContentProvider insert $uri")
        return getPluginProvider(uri)?.insert(uri, values) ?: return null
    }

    override fun query(uri: Uri, projection: Array<String>?, selection: String?,
                       selectionArgs: Array<String>?, sortOrder: String?): Cursor? {
        println("ArmourContentProvider query $uri")
        return getPluginProvider(uri)?.query(uri, projection, selection, selectionArgs, sortOrder)
    }

    override fun update(uri: Uri, values: ContentValues?, selection: String?,
                        selectionArgs: Array<String>?): Int {
        println("ArmourContentProvider update $uri")
        return getPluginProvider(uri)?.update(uri, values, selection, selectionArgs) ?: return 0
    }

    val map = mutableMapOf<String, ContentProvider>()

    fun getPluginProvider(uri: Uri): ContentProvider? {
        val module = uri.getQueryParameter(MODULE)
        val name = uri.getQueryParameter(COMPONENT)
        val uri = uri.getQueryParameter(PLUGIN_URI)

        println("$module :: $name :: $uri")

        if (map[name] != null) return map[name]

        val armour = Armour.instance() ?: return null
        val realComponent = Navigator.instance.getModule(module)?.getComponent(name)?.realComponent ?: return null
        val componentInstance = (armour.getPlugin(module)?.classloader?.loadClass(realComponent)?.newInstance() ?: return null) as? ContentProvider ?: return null
        map[name] = componentInstance // todo name 作为key可能存在问题
        return componentInstance
    }
}
