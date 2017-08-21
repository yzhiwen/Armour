package com.yangzhiwen.armourplugin

/**
 * Created by yangzhiwen on 17/8/17.
 */
import org.gradle.api.Plugin
import org.gradle.api.Project

class AgPlugin implements Plugin<Project> {

    @Override
    void apply(Project project) {
        System.out.println("========================")
        System.out.println("hello gradle plugin!")
        File manifestFile = project.android.sourceSets.main.manifest.srcFile
        println manifestFile.path
        def manifest = new XmlSlurper().parse(manifestFile)

        String pkg = manifest.@package
        manifest.application.activity.each {
            String name = it.'@android:name'

            if (name.substring(0, 1) == '.') {
                name = pkg + name
            }
            println(name)

            def actions = it.'intent-filter'.'action'
            for (action in actions) {
                println(action.'@android:name')
            }
        }

        manifest.application.receiver.each {
            String name = it.'@android:name'

            if (name.substring(0, 1) == '.') {
                name = pkg + name
            }
            println(name)

            def actions = it.'intent-filter'.'action'
            for (action in actions) {
                println(action.'@android:name')
            }
        }


        manifest.application.service.each {
            String name = it.'@android:name'
            String process = it."@android:process"
            if (name.substring(0, 1) == '.') {
                name = pkg + name
            }
            println(name + " || " + process)
        }


        manifest.application.provider.each {
            String name = it.'@android:name'
            String authorities = it."@android:authorities"
            if (name.substring(0, 1) == '.') {
                name = pkg + name
            }
            println(name + " || " + authorities)
        }
        System.out.println("========================")
    }
}