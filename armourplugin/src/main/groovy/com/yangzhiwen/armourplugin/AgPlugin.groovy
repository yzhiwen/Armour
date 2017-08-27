package com.yangzhiwen.armourplugin

import groovy.json.JsonOutput

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

        Map<Object, Object> map = new HashMap<>()
        File manifestFile = project.android.sourceSets.main.manifest.srcFile
        println manifestFile.path
        def manifest = new XmlSlurper().parse(manifestFile)

        def activityList = []
        String pkg = manifest.@package
        manifest.application.activity.each {
            String name = it.'@android:name'
            if (name.substring(0, 1) == '.') {
                name = pkg + name
            }
            println(name)
            def isMain = false
            def actions = it.'intent-filter'.'action'
            for (action in actions) {
                println(action.'@android:name')
                if (action.'@android:name' == "android.intent.action.MAIN") {
                    isMain = true
                }
            }
            activityList.add(["name": name, "isMain": isMain])
        }

        def receiverList = []
        manifest.application.receiver.each {
            String name = it.'@android:name'

            if (name.substring(0, 1) == '.') {
                name = pkg + name
            }
            println(name)

            def actionList = []
            def actions = it.'intent-filter'.'action'
            for (action in actions) {
                def aa = action.'@android:name'.text()
                actionList.add(aa)
            }
            receiverList.add(["name": name, "action": actionList])
        }

        def serviceList = []
        manifest.application.service.each {
            String name = it.'@android:name'
            String process = it."@android:process"
            if (name.substring(0, 1) == '.') {
                name = pkg + name
            }
            def remote = false
            if (process != "") remote = true
            println(name + " || " + process)
            serviceList.add(["name": name, "remote": remote])
        }

        def providerList = []
        manifest.application.provider.each {
            String name = it.'@android:name'
            String authorities = it."@android:authorities"
            if (name.substring(0, 1) == '.') {
                name = pkg + name
            }
            println(name + " || " + authorities)
            providerList.add(["name": name, "url": authorities])
        }

        map.put("module", "user_center")
        println activityList
        println JsonOutput.toJson(activityList)
        map.put("activity", activityList)
        map.put("receiver", receiverList)
        map.put("service", serviceList)
        map.put("provider", providerList)

        def json = JsonOutput.toJson(map)
        println json

        File mainFile = manifestFile.getParentFile()
        println(mainFile.path)
        File assets = new File(mainFile, "assets")
        if (!assets.exists()) assets.mkdirs()
        File armourConfig = new File(assets, "ArmourConfig.json")
        if (!armourConfig.exists()) armourConfig.createNewFile()
        armourConfig.write(json)
        System.out.println("========================")
    }
}