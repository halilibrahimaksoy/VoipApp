package com.haksoy.soip.utlis

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.os.Build
import androidx.core.app.NotificationCompat
import com.haksoy.soip.MainApplication
import com.haksoy.soip.R
import com.haksoy.soip.data.database.ChatRepository
import com.haksoy.soip.ui.main.MainActivity
import java.util.concurrent.Executors

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

    private val chatRepository = ChatRepository.getInstance(
        MainApplication.instance.applicationContext,
        Executors.newSingleThreadExecutor()
    )

    private var manager: NotificationManager? = null
        get() {
            if (field == null) {
                field =
                    context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            }
            return field
        }

    fun sendNotification(userUid: String, userName: String, messageText: String) {
        val intent = Intent(context, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        intent.putExtra(Constants.ConversationDetailFragmentSelectedUser, userUid)
        val pendingIntent = PendingIntent.getActivity(
            context, 0 /* Request code */, intent,
            PendingIntent.FLAG_ONE_SHOT
        )

        val channelId = Constants.NotificationChannelID
        val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val inboxStyle = NotificationCompat.InboxStyle()
        val notificationBuilder = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setAutoCancel(true)
            .setSound(defaultSoundUri)
            .setVibrate(longArrayOf(300, 300))
            .setContentIntent(pendingIntent)

        chatRepository.getUnreadConversation(userUid).observeOnce {
            for (chat in it) {
                inboxStyle.addLine(chat.text)
            }

            notificationBuilder.setContentTitle(getUserNameWithNumOfMessages(it.size, userName))
            notificationBuilder.setStyle(inboxStyle)

            // Since android Oreo notification channel is needed.
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val channel = NotificationChannel(
                    channelId,
                    "Channel human readable title",
                    NotificationManager.IMPORTANCE_HIGH
                )
                manager!!.createNotificationChannel(channel)
            }

            manager!!.notify(
                userUid.hashCode()/* ID of notification */,
                notificationBuilder.build()
            )
        }
    }

    private fun getUserNameWithNumOfMessages(
        unreadCount: Int,
        userName: String
    ): String {
        return if (unreadCount == 0 || unreadCount == 1) userName else "$userName ($unreadCount Messages) "
    }

    fun removeNotification(userUid: String) {
        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.cancel(userUid.hashCode())
    }
}