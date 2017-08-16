package com.yangzhiwen.armour.ext.compass

import android.content.ContentValues
import android.content.ServiceConnection
import android.database.Cursor
import android.net.Uri
import com.yangzhiwen.armour.ArmourService
import com.yangzhiwen.armour.compass.ComponentOperation

/**
 * Created by yangzhiwen on 2017/8/17.
 */
class StartActivityOperation : ComponentOperation() {
    companion object {
        val instance = StartActivityOperation()
    }
}

class SendReceiverOperation : ComponentOperation() {
    companion object {
        val instance = SendReceiverOperation()
    }
}

class StartServiceOperation : ComponentOperation(ArmourService.START) {
    companion object {
        val instance = StartServiceOperation()
    }
}

class StopServiceOperation : ComponentOperation(ArmourService.STOP) {
    companion object {
        val instance = StopServiceOperation()
    }
}

class BindServiceOperation(val sc: ServiceConnection) : ComponentOperation(ArmourService.BIND)
class UnbindServiceOperation(val sc: ServiceConnection) : ComponentOperation(ArmourService.UNBIND)

class InsertContentOperation(val url: Uri, val values: ContentValues) : ComponentOperation()
class DeleteContentOperation(val url: Uri, val where: String, val selectionArgs: Array<out String>) : ComponentOperation()
class QueryContentOperation(val url: Uri, val projection: Array<out String>, val selection: String, val selectionArgs: Array<out String>, val sortOrder: String, val callback: QueryCallback) : ComponentOperation()

interface QueryCallback {
    fun onQuery(cursor: Cursor)
}