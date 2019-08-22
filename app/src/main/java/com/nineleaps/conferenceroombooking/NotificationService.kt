package com.nineleaps.conferenceroombooking

import android.util.Log
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class NotificationService : FirebaseMessagingService() {
    override fun onMessageReceived(p0: RemoteMessage) {

        Log.i("NOTIFICATION",p0.toString())
        // Check if message contains a data payload.
        if (p0.data.isNotEmpty()) {
//            Log.i("Notification", p0.notification!!.body.toString())
        }
    }
}
