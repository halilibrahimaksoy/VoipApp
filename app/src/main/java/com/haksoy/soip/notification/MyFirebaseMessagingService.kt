package com.haksoy.soip.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.google.gson.Gson
import com.haksoy.soip.MainApplication
import com.haksoy.soip.R
import com.haksoy.soip.data.FirebaseDao
import com.haksoy.soip.data.chat.Chat
import com.haksoy.soip.data.database.ChatRepository
import com.haksoy.soip.data.database.UserRepository
import com.haksoy.soip.data.notification.NotificationChat
import com.haksoy.soip.data.notification.NotificationChatType
import com.haksoy.soip.data.notification.NotificationData
import com.haksoy.soip.data.notification.NotificationType
import com.haksoy.soip.ui.splash.SplashActivity
import com.haksoy.soip.utlis.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.util.concurrent.Executors


class MyFirebaseMessagingService : FirebaseMessagingService() {
    override fun onMessageReceived(p0: RemoteMessage) {
        super.onMessageReceived(p0)
        val notificationData = Gson().fromJson<NotificationData>(
            p0.data.toString(),
            NotificationData::class.java
        )
        when (notificationData.notificationType) {
            NotificationType.CHAT -> {
                handleChatNotificationData(notificationData)
            }
        }

    }

    private fun handleChatNotificationData(notificationData: NotificationData) {
        val notificationChat = Gson().fromJson<NotificationChat>(
            notificationData.content.toString(),
            NotificationChat::class.java
        )
        when (notificationChat.chatType) {
            NotificationChatType.NEW -> {
                handleNewChat(notificationChat)
            }
            NotificationChatType.DELETE -> {
                handleRemoveChat(notificationChat)
            }
        }
    }

    private fun handleRemoveChat(notificationChat: NotificationChat) {
        val incomingChat: Chat = notificationChat.chat
        chatRepository.removeChat(incomingChat)
        removeNotification(incomingChat.uid.hashCode())
    }

    private fun handleNewChat(notificationChat: NotificationChat) {
        val incomingChat: Chat = notificationChat.chat
        chatRepository.addChat(incomingChat)

        GlobalScope.launch(Dispatchers.Main) {
            if (!isAppInForeground()) {
                userRepository.getUser(incomingChat.userUid).observeOnce {
                    if (it.status == Resource.Status.SUCCESS) {
                        sendNotification(
                            incomingChat.uid.hashCode(),
                            it.data!!.name!!,
                            incomingChat.text!!
                        )
                    }
                }

            } else {
                try {
                    val notification: Uri =
                        RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
                    val r = RingtoneManager.getRingtone(
                        applicationContext,
                        notification
                    )
                    r.play()
                    MainApplication.instance.vibratePhone()
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }

    override fun onNewToken(p0: String) {
        super.onNewToken(p0)
        if (FirebaseDao.getInstance().isAuthUserExist())
            FirebaseDao.getInstance().updateToken(p0)
        putPreferencesString(Constants.FIREBASE_MESSAGING_TOKEN, p0)
    }

    private val chatRepository = ChatRepository.getInstance(
        MainApplication.instance.applicationContext,
        Executors.newSingleThreadExecutor()
    )
    private val userRepository = UserRepository.getInstance(
        MainApplication.instance.applicationContext,
        Executors.newSingleThreadExecutor()
    )

    private fun sendNotification(id: Int, messageTitle: String, messageBody: String) {
        val intent = Intent(this, SplashActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        val pendingIntent = PendingIntent.getActivity(
            this, 0 /* Request code */, intent,
            PendingIntent.FLAG_ONE_SHOT
        )

        val channelId = getString(R.string.default_notification_channel_id)
        val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val notificationBuilder = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle(messageTitle)
            .setContentText(messageBody)
            .setAutoCancel(true)
            .setSound(defaultSoundUri)
            .setVibrate(longArrayOf(300))
            .setContentIntent(pendingIntent)

        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

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

    private fun removeNotification(id: Int) {
        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.cancel(id)
    }

    companion object {

        private const val TAG = "MyFirebaseMsgService"
    }
}