package com.neosafe.neophone.helpers

import android.content.Context
import android.net.Uri
import android.os.Handler
import android.os.Looper
import android.telecom.Call
import android.telecom.InCallService
import android.telecom.VideoProfile
import com.neosafe.commons.extensions.getMyContactsCursor
import com.neosafe.commons.helpers.MyContactsContentProvider
import com.neosafe.commons.helpers.SimpleContactsHelper
import com.neosafe.commons.helpers.ensureBackgroundThread
import com.neosafe.neophone.models.CallContact

// inspired by https://github.com/Chooloo/call_manage
class CallManager {
    companion object {
        var call: Call? = null
        var inCallService: InCallService? = null

        fun accept() {
            call?.answer(VideoProfile.STATE_AUDIO_ONLY)
        }

        fun reject() {
            if (call != null) {
                if (call!!.state == Call.STATE_RINGING) {
                    call!!.reject(false, null)
                } else {
                    call!!.disconnect()
                }
            }
        }

        fun registerCallback(callback: Call.Callback) {
            if (call != null) {
                call!!.registerCallback(callback)
            }
        }

        fun unregisterCallback(callback: Call.Callback) {
            call?.unregisterCallback(callback)
        }

        fun getState() = if (call == null) {
            Call.STATE_DISCONNECTED
        } else {
            call!!.state
        }

        fun keypad(c: Char) {
            call?.playDtmfTone(c)
            call?.stopDtmfTone()
        }

        fun getCallContact(context: Context, callback: (CallContact?) -> Unit) {
            ensureBackgroundThread {
                val callContact = CallContact("", "", "")
                if (call == null || call!!.details == null || call!!.details!!.handle == null) {
                    callback(callContact)
                    return@ensureBackgroundThread
                }

                val uri = Uri.decode(call!!.details.handle.toString())
                if (uri.startsWith("tel:")) {
                    val number = uri.substringAfter("tel:")
                    callContact.number = number
                    callContact.name = SimpleContactsHelper(context).getNameFromPhoneNumber(number)
                    callContact.photoUri = SimpleContactsHelper(context).getPhotoUriFromPhoneNumber(number)

                    if (callContact.name != callContact.number) {
                        callback(callContact)
                    } else {
                        Handler(Looper.getMainLooper()).post {
                            val privateCursor = context.getMyContactsCursor(false, true)?.loadInBackground()
                            ensureBackgroundThread {
                                val privateContacts = MyContactsContentProvider.getSimpleContacts(context, privateCursor)
                                val privateContact = privateContacts.firstOrNull { it.doesContainPhoneNumber(callContact.number) }
                                if (privateContact != null) {
                                    callContact.name = privateContact.name
                                }
                                callback(callContact)
                            }
                        }
                    }
                }
            }
        }
    }
}
