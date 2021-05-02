package com.haksoy.soip.messaging

import android.content.Context
import android.media.RingtoneManager
import android.net.Uri
import com.haksoy.soip.MainApplication
import com.haksoy.soip.data.chat.Chat
import com.haksoy.soip.data.database.ChatRepository
import com.haksoy.soip.data.database.UserRepository
import com.haksoy.soip.data.message.ChatEventType
import com.haksoy.soip.data.message.MessageChat
import com.haksoy.soip.utlis.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.util.concurrent.Executors

class MessageHandler(val context: Context) {

    private val chatRepository = ChatRepository.getInstance(
            MainApplication.instance.applicationContext,
            Executors.newSingleThreadExecutor()
    )
    private val userRepository = UserRepository.getInstance(
            MainApplication.instance.applicationContext,
            Executors.newSingleThreadExecutor()
    )

    fun handleChatNotificationData(messageData: MessageChat) {
        when (messageData.chatEventType) {
            ChatEventType.NEW -> {
                handleNewChat(messageData.chat)
            }
            ChatEventType.DELETE -> {
                handleRemoveChat(messageData.chat)
            }
        }
    }

    private fun handleRemoveChat(chat: Chat) {
        chatRepository.removeChat(chat)
        NotificationHelper.getInstance(context).removeNotification(chat.userUid)
    }

    private fun handleNewChat(chat: Chat) {
        chatRepository.addChat(chat)

        GlobalScope.launch(Dispatchers.Main) {
            if (!context.isAppInForeground()) {
                userRepository.getUser(chat.userUid).observeOnce {
                    if (it.status == Resource.Status.SUCCESS) {
                        NotificationHelper.getInstance(context)
                                .sendNotification(
                                        it.data!!.uid,
                                        it.data.name!!,
                                        chat.text!!
                                )
                    }
                }

            } else {
                try {
                    val notification: Uri =
                            RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
                    val r = RingtoneManager.getRingtone(
                            context,
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
}