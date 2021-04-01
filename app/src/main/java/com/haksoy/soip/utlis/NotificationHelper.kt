package com.haksoy.soip.utlis

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.os.Build
import androidx.core.app.NotificationCompat
import com.haksoy.soip.R
import com.haksoy.soip.ui.main.MainActivity

class NotificationHelper(private val context: Context) {
    companion object {
        @Volatile
        private var INSTANCE: NotificationHelper? = null

        fun getInstance(context: Context): NotificationHelper {
            return INSTANCE
                ?: synchronized(this) {
                    INSTANCE
                        ?: NotificationHelper(
                            context
                        )
                            .also { INSTANCE = it }
                }
        }
    }

    fun sendNotification(id: Int, userUid: String, userName: String, messageText: String) {
        val intent = Intent(context, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        intent.putExtra(Constants.ConversationDetailFragmentSelectedUser, userUid)
        val pendingIntent = PendingIntent.getActivity(
            context, 0 /* Request code */, intent,
            PendingIntent.FLAG_ONE_SHOT
        )

        val channelId = Constants.NotificationChannelID
        val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val notificationBuilder = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle(userName)
            .setContentText(messageText)
            .setAutoCancel(true)
            .setSound(defaultSoundUri)
            .setVibrate(longArrayOf(300,300))
            .setContentIntent(pendingIntent)

        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Since android Oreo notification channel is needed.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Channel human readable title",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            notificationManager.createNotificationChannel(channel)
        }

        notificationManager.notify(id/* ID of notification */, notificationBuilder.build())
    }
}