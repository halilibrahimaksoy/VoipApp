package com.haksoy.soip.messaging

import android.media.RingtoneManager
import android.net.Uri
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.google.gson.Gson
import com.haksoy.soip.MainApplication
import com.haksoy.soip.data.FirebaseDao
import com.haksoy.soip.data.chat.Chat
import com.haksoy.soip.data.database.ChatRepository
import com.haksoy.soip.data.database.UserRepository
import com.haksoy.soip.data.message.ChatEventType
import com.haksoy.soip.data.message.EventType
import com.haksoy.soip.data.message.MessageChat
import com.haksoy.soip.data.message.MessageData
import com.haksoy.soip.utlis.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.util.concurrent.Executors


class MyFirebaseMessagingService : FirebaseMessagingService() {
    override fun onMessageReceived(p0: RemoteMessage) {
        super.onMessageReceived(p0)
        val notificationData = Gson().fromJson<MessageData>(
            p0.data.toString(),
            MessageData::class.java
        )
        when (notificationData.event) {
            EventType.CHAT -> {
                handleChatNotificationData(notificationData)
            }
        }

    }

    private fun handleChatNotificationData(messageData: MessageData) {
        val notificationChat = Gson().fromJson<MessageChat>(
            messageData.content.toString(),
            MessageChat::class.java
        )
        when (notificationChat.chatEventType) {
            ChatEventType.NEW -> {
                handleNewChat(notificationChat)
            }
            ChatEventType.DELETE -> {
                handleRemoveChat(notificationChat)
            }
        }
    }

    private fun handleRemoveChat(messageChat: MessageChat) {
        val incomingChat: Chat = messageChat.chat
        chatRepository.removeChat(incomingChat)
        NotificationHelper.getInstance(this).removeNotification(incomingChat.userUid)
    }

    private fun handleNewChat(messageChat: MessageChat) {
        val incomingChat: Chat = messageChat.chat
        chatRepository.addChat(incomingChat)

        GlobalScope.launch(Dispatchers.Main) {
            if (!isAppInForeground()) {
                userRepository.getUser(incomingChat.userUid).observeOnce {
                    if (it.status == Resource.Status.SUCCESS) {
                        NotificationHelper.getInstance(this@MyFirebaseMessagingService)
                            .sendNotification(
                                it.data!!.uid,
                                it.data.name!!,
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

    companion object {

        private const val TAG = "MyFirebaseMsgService"
    }
}