package com.yangzhiwen.armour.compass

/**
 * Created by yangzhiwen on 2017/8/12.
 */
interface NavigatorInterceptor {

    fun onIntercept(original: NavigatorComponent, current: NavigatorComponent)

}