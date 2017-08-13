package com.yangzhiwen.compass

/**
 * Created by yangzhiwen on 2017/8/12.
 */
interface NavigatorInterceptor {

    fun onInterce(original: NavigatorComponent, current: NavigatorComponent)

}