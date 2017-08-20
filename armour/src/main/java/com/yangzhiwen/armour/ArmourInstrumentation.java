package com.yangzhiwen.armour;

import android.app.Activity;
import android.app.Instrumentation;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;

import com.yangzhiwen.armour.ext.Hacker;


/**
 * use java，because kotlin Instrumentation execStartActivity can not invoke by system
 * Created by yangzhiwen on 17/8/18.
 */
public class ArmourInstrumentation extends Instrumentation {

    private Instrumentation base;
    private Armour armour;

    public ArmourInstrumentation(Armour armour, Instrumentation base) {
        this.base = base;
        this.armour = armour;
    }

    public ActivityResult execStartActivity(Context who, IBinder contextThread, IBinder token, Activity target,
                                            Intent intent, int requestCode, Bundle options) {
        intent = armour.getArmourHacker().execStartActivity(who, intent);
        return execBaseStartActivity(who, contextThread, token, target, intent, requestCode, options);
    }

    public ActivityResult execBaseStartActivity(
            Context who, IBinder contextThread, IBinder token, Activity target,
            Intent intent, int requestCode, Bundle options) {
        Class[] parameterTypes = {Context.class, IBinder.class, IBinder.class, Activity.class, Intent.class,
                int.class, Bundle.class};
        try {
            return (ActivityResult) Hacker.Companion
                    .on(Instrumentation.class)
                    .method("execStartActivity", parameterTypes)
                    .invoke(base, who, contextThread, token, target, intent, requestCode, options);
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public Activity newActivity(ClassLoader cl, String className, Intent intent) throws InstantiationException, IllegalAccessException, ClassNotFoundException {
        Activity activity = armour.getArmourHacker().newActivity(className, intent);
        if (activity != null) return activity;
        return super.newActivity(cl, className, intent);
    }

    @Override
    public void callActivityOnCreate(Activity activity, Bundle icicle) {
        armour.getArmourHacker().callActivityOnCreate(activity);
        super.callActivityOnCreate(activity, icicle);
    }
}

