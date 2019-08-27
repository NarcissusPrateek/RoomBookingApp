package com.nineleaps.conferenceroombooking


import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.net.Uri
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.nineleaps.conferenceroombooking.recurringMeeting.ui.UpcomingBookingFragment
import java.util.*


class NotificationService : FirebaseMessagingService() {
    override fun onMessageReceived(p0: RemoteMessage) {


        val m = (Date().time / 1000L % Integer.MAX_VALUE)  as Int
        val intent = Intent(this,UpcomingBookingFragment::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        val pendingIntent = PendingIntent.getActivity(this,m,intent,PendingIntent.FLAG_ONE_SHOT)
        val channelId = "Default"


        val soundUri: Uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)

        val notificationBuilder = NotificationCompat.Builder(this)
            .setSmallIcon(R.drawable.ic_menu_send)  //a resource for your custom small icon
            .setContentTitle(p0.getData().get("title")) //the "title" value you sent in your notification
            //.setContentText(p0.getData().get("message")) //ditto
            .setAutoCancel(true)  //dismisses the notification on click
            .setSound(soundUri)
            .setContentIntent(pendingIntent)
        val notificationManager:NotificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O){
            val channel:NotificationChannel = NotificationChannel(channelId,"Default channel",NotificationManager.IMPORTANCE_DEFAULT)
            notificationManager.createNotificationChannel(channel)
        }

        notificationManager.notify(0,notificationBuilder.build())
    }
}
