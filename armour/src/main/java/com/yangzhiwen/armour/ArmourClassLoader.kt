package com.yangzhiwen.armour


/**
 * Created by yangzhiwen on 2017/8/13.
 */
class ArmourClassLoader(parent: ClassLoader) : ClassLoader(parent) {

    /**
     * class first load
     */
    override fun loadClass(name: String?): Class<*> {
        println("============= || load class: $name")
        if (name.equals("com.yangzhiwen.navigator.ProxyActivity")) {
            val armour = Armour.instance() ?: return super.loadClass(name)
            val plugin = armour.getPlugin("user_center") ?: return super.loadClass(name)
            return plugin.classloader.loadClass("com.yangzhiwen.newdemo.CenterActivity")
        }
        return super.loadClass(name)
    }
}