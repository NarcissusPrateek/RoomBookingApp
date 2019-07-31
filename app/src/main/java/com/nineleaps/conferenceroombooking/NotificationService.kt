package com.nineleaps.conferenceroombooking

import android.util.Log
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class NotificationService : FirebaseMessagingService() {
    override fun onMessageReceived(remoteMessage: RemoteMessage?) {
        // Check if message contains a data payload.
        if (remoteMessage!!.data.isNotEmpty()) {
            Log.i("-------------", remoteMessage.notification!!.body.toString())
        }
    }
}
