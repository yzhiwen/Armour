package com.yangzhiwen.armour;

import android.app.Activity;
import android.app.Application;
import android.app.Instrumentation;
import android.content.ContentProvider;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.IBinder;

import java.lang.reflect.Constructor;


/**
 * use java，because kotlin Instrumentation execStartActivity can not invoke by system
 * Created by yangzhiwen on 17/8/18.
 */
public class ArmourInstrumentation extends Instrumentation {

    public static void tt() throws Exception {
        String value = "android.app.ActivityThread$ProviderClientRecord";
        System.out.println(String[].class);
        Class<?> b = Class.forName("android.content.IContentProvider");
        System.out.println(b);
        Class<?> c = Class.forName("android.app.IActivityManager$ContentProviderHolder");
        System.out.println(c);
        System.out.println(ContentProvider.class);
        Class<?> abc = Class.forName(value);
        System.out.println(abc.getConstructors());
        Constructor<?>[] constructors = abc.getDeclaredConstructors();
        System.out.println(constructors.length);
//        String[] sss = {""};
        constructors[0].setAccessible(true);
        // 五个参数因为是外部类是ActivityThread
        Object o = constructors[0].newInstance(null, null, null, null, null);
        System.out.println(o);
        System.out.println(constructors[0]);
//        System.out.println("====");
//        abc.newInstance();
//                .getConstructor(String[].class, b, ContentProvider.class, c);
    }

    private Instrumentation base;
    private Armour armour;

    public ArmourInstrumentation(Armour armour, Instrumentation base) {
        this.base = base;
        this.armour = armour;
    }

    public ActivityResult execStartActivity(Context who, IBinder contextThread, IBinder token, Activity target,
                                            Intent intent, int requestCode, Bundle options) {
        intent = armour.getArmourHooker().execStartActivity(who, intent);
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
//            e.printStackTrace();
            return null;
        }
    }

    @Override
    public Activity newActivity(ClassLoader cl, String className, Intent intent) throws InstantiationException, IllegalAccessException, ClassNotFoundException {
        Activity activity = armour.getArmourHooker().newActivity(cl, className, intent);
        if (activity != null) return activity;
        return super.newActivity(cl, className, intent);
    }

    @Override
    public void callActivityOnCreate(Activity activity, Bundle icicle) {
        armour.getArmourHooker().callActivityOnCreate(activity);
        super.callActivityOnCreate(activity, icicle);
    }
}

