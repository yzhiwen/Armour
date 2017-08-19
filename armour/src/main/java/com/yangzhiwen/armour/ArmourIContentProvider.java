package com.yangzhiwen.armour;

import android.net.Uri;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.Arrays;

/**
 * Created by yangzhiwen on 2017/8/19.
 */

public class ArmourIContentProvider implements InvocationHandler {

    private Object icp;

    public ArmourIContentProvider(Object icp) {
        this.icp = icp;
    }

    @Override
    public Object invoke(Object o, Method method, Object[] objects) throws Throwable {
        System.out.println("ArmourIContentProvider invoke " + method.getName());

        if (objects != null) {
            System.out.println(Arrays.toString(objects));
            int index = -1;
            String arg = null;
            for (Object obj : objects) {
                index++;
                if (obj != null && obj instanceof Uri) {
                    String str = obj.toString();
                    if (str.equals("content://com.yangzhiwen.demo")) {
                        System.out.println("== demo");
                        arg = "content://com.yangzhiwen.armour";
                        break;
                    } else {
                        System.out.println("!= demo");
                    }
                } else {
                    System.out.println("obj == null");
                }
            }
            if (index != -1) {
                objects[index] = Uri.parse(arg);
                System.out.println("== index == " + index + " || " + arg);
            } else {
                System.out.println("== index == -1");
            }
            System.out.println(Arrays.toString(objects));
        }

        return method.invoke(icp, objects);
    }
}
