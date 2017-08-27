# Armour 特性
- 支持四大组件

## 插件开发
- 插件的开发同app开发
- 只需要依赖armour gradle plugin即可，armour gradle plugin会在assets生成ArmourConfig.json组件信息用于宿主加载
```
dependencies {
    classpath 'com.yangzhiwen.armour:armourplugin:0.1.0'
}


apply plugin: 'com.yangzhiwen.armourplugin'
```

## 宿主开发
- 依赖armour
- 插件安装
```kotlin
val pluginPath = download()
Armour.instance(application).instantPlugin(pluginName, pluginPath)
```
- 插件启动
```kotlin
Armour.instance(application).getPlugin(pluginName)?.start()
```

# Article
[插件化专题](http://www.jianshu.com/c/f313e29e8ead)
