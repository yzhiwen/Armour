package com.yangzhiwen.armour.ext

import java.lang.reflect.Field
import java.lang.reflect.Method

/**
 * Created by yangzhiwen on 17/8/15.
 */
class Hacker private constructor(val cl: Class<*>) {

    companion object {
        // todo 做缓存
        fun on(cl: Class<*>) = Hacker(cl)
    }

    fun field(name: String): Field? {
        val field = findField(cl, name)
        field?.isAccessible = true
        return field
    }

    fun method(name: String, vararg params: Class<*>): Method? {
        val method = cl.getDeclaredMethod(name, *params)
        method.isAccessible = true
        return method
    }

    fun findField(aClass: Class<*>?, name: String): Field? {
        try {
            return aClass?.getDeclaredField(name) ?: return null
        } catch (e: Exception) {
            return findField(aClass?.superclass, name) ?: return null
        }
    }

}