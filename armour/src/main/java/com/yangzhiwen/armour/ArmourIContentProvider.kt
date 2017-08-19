//package com.yangzhiwen.armour
//
//import java.lang.reflect.InvocationHandler
//import java.lang.reflect.InvocationTargetException
//import java.lang.reflect.Method
//
///**
// * Created by yangzhiwen on 2017/8/19.
// */
//class ArmourIContentProvider(val icp: Any?) : InvocationHandler {
//    var count = 0
//
//    override fun invoke(proxy: Any?, method: Method, args: Array<out Any>): Any? {
//        println("ArmourIContentProvider invoke - $method - $args")
//
//
//        try {
//            return method.invoke(icp, args)
//        } catch (e: InvocationTargetException) {
//            e.printStackTrace()
//            throw e.getTargetException()
//        }
//
//
////        if (args == null) return method.invoke(icp)
////        else return method.invoke(icp, args)
//    }
//}