package com.yangzhiwen.armour;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.Arrays;

/**
 * use java
 * Created by yangzhiwen on 2017/8/19.
 */

public class ArmourIContentProvider implements InvocationHandler {

    private ArmourHacker armourHacker;
    /**
     * IContentProvider
     */
    private Object icp;

    public ArmourIContentProvider(Object icp, ArmourHacker armourHacker) {
        this.icp = icp;
        this.armourHacker = armourHacker;
    }

    @Override
    public Object invoke(Object o, Method method, Object[] objects) throws Throwable {
        System.out.println("ArmourIContentProvider invoke " + method.getName() + Arrays.toString(objects));

        armourHacker.onIContentProviderInvoke(objects);


        return method.invoke(icp, objects);
    }
}
