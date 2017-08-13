package com.yangzhiwen.armour

import android.content.Context
import dalvik.system.DexClassLoader

/**
 * Created by yangzhiwen on 2017/8/13.
 */
class APlugin(context: Context, name: String, path: String) {
    val DEX_OUT_PATH = context.getDir("aplugin", Context.MODE_PRIVATE).absolutePath
    val classloader = DexClassLoader(path, DEX_OUT_PATH, null, context.classLoader)

    init {

    }

}