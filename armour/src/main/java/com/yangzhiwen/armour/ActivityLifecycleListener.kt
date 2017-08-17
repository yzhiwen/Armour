package com.yangzhiwen.armour

import android.app.Activity
import android.app.Application
import android.os.Bundle

/**
 * Created by yangzhiwen on 17/8/15.
 */
@Deprecated("delete")
class ActivityLifecycleListener : Application.ActivityLifecycleCallbacks {

    companion object {
        val instance = ActivityLifecycleListener()
    }

    override fun onActivityResumed(activity: Activity?) {
    }

    override fun onActivityStarted(activity: Activity?) {
    }

    override fun onActivityDestroyed(activity: Activity?) {
    }

    override fun onActivitySaveInstanceState(activity: Activity?, bundle: Bundle?) {
    }

    override fun onActivityStopped(activity: Activity?) {
    }

    override fun onActivityCreated(activity: Activity?, bundle: Bundle?) {
        if (activity != null) ArmourHacker.instance.hookActivityResource(activity)
    }

    override fun onActivityPaused(activity: Activity?) {
    }
}