package com.neosafe.neophone.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.neosafe.neophone.helpers.ACCEPT_CALL
import com.neosafe.neophone.helpers.CallManager
import com.neosafe.neophone.helpers.DECLINE_CALL

class CallActionReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        when (intent.action) {
            ACCEPT_CALL -> CallManager.accept()
            DECLINE_CALL -> CallManager.reject()
        }
    }
}
