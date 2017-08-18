package com.yangzhiwen.armour


/**
 * Created by yangzhiwen on 2017/8/13.
 */
class ArmourClassLoader(parent: ClassLoader) : ClassLoader(parent) {

    /**
     * class first load
     */
//    override fun loadClass(name: String?): Class<*> {
//        println("**********************************************")
//        println("ArmourClassLoader ____ load class: $name")
//        println("**********************************************")
////        return Armour.instance()?.classLoaderInterceptor?.onLoadClass(name) ?: super.loadClass(name)
//        return loadClass(name)
//    }
}