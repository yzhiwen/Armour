package com.yangzhiwen.armour.ext.compass

import com.yangzhiwen.armour.compass.ComponentType

/**
 * Created by yangzhiwen on 2017/8/12.
 */

val ComponentType.Activity: String
    get() = "ActivityType"

val ComponentType.Service: String
    get() = "ServiceType"


val ComponentType.Receiver: String
    get() = "ReceiverType"


val ComponentType.Provider: String
    get() = "ProviderType"

