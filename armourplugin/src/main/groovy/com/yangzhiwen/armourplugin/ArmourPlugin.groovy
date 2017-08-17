package com.yangzhiwen.armourplugin;

/**
 * Created by yangzhiwen on 17/8/17.
 */
import org.gradle.api.Plugin;
import org.gradle.api.Project;

public class ArmourPlugin implements Plugin<Project> {

    @Override void apply(Project project) {
        System.out.println("========================");
        System.out.println("hello gradle plugin!");
        System.out.println("========================");
        project.dependencies {
              // compile files('libs/classes.jar')
              compile 'org.greenrobot:eventbus:3.0.0'
        }
    }
}