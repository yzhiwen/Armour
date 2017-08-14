package com.yangzhiwen.navigator.ext.navigator

import com.yangzhiwen.compass.ComponentType

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

