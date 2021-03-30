package com.haksoy.soip.ui.conversationDetail

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.haksoy.soip.data.FirebaseDao
import com.haksoy.soip.data.chat.Chat
import com.haksoy.soip.data.chat.ChatDirection
import com.haksoy.soip.data.chat.ChatType
import com.haksoy.soip.data.database.ChatRepository
import com.haksoy.soip.data.notification.NotificationRepository
import com.haksoy.soip.data.user.User
import java.util.*
import java.util.concurrent.Executors

class ConversationDetailViewModel(application: Application) : AndroidViewModel(application) {
    private val firebaseDao = FirebaseDao.getInstance()
    private val chatRepository = ChatRepository.getInstance(
        application.applicationContext,
        Executors.newSingleThreadExecutor()
    )
    private val notificationRepository = NotificationRepository.getInstance(
        Executors.newSingleThreadExecutor()
    )

    lateinit var user: User
    lateinit var conversationDetailList: LiveData<List<Chat>>
    fun getConversationDetail(uid: String) {
        conversationDetailList = chatRepository.getConversationDetails(uid)
    }


    fun sendChat(message: String) {

        val localChat = Chat(
            UUID.randomUUID().toString(),
            user.uid,
            ChatDirection.OutGoing,
            true,
            ChatType.TEXT,
            message,
            null,
            Date().time,
            null
        )
        chatRepository.addChat(localChat)

        val remoteChat = Chat(
            localChat.uid,
            firebaseDao.getCurrentUserUid(),
            ChatDirection.InComing,
            false,
            ChatType.TEXT,
            message,
            null,
            localChat.createDate,
            null
        )

        if (!user.token.isNullOrEmpty())
            notificationRepository.sendChat(user.token.toString(), remoteChat)
    }


    fun removeChatAtPosition(position: Int) {
        chatRepository.removeChat(conversationDetailList.value!![position])
    }

    fun getChatDirection(position: Int): ChatDirection =
        conversationDetailList.value!![position].direction

    fun sendRemoveRequestAtPosition(position: Int) {
        notificationRepository.removeChat(
            user.token.toString(),
            conversationDetailList.value!![position]
        )
    }
}
