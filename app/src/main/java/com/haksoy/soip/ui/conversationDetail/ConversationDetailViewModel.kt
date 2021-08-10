package com.haksoy.soip.ui.conversationDetail

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.haksoy.soip.data.FirebaseDao
import com.haksoy.soip.data.chat.Chat
import com.haksoy.soip.data.chat.ChatDirection
import com.haksoy.soip.data.chat.ChatType
import com.haksoy.soip.data.database.ChatRepository
import com.haksoy.soip.data.user.User
import com.haksoy.soip.messaging.MessageRepository
import java.util.*
import java.util.concurrent.Executors

private const val TAG = "SoIP:ConversationDetailViewModel"

class ConversationDetailViewModel(application: Application) : AndroidViewModel(application) {
    private val firebaseDao = FirebaseDao.getInstance()
    private val chatRepository = ChatRepository.getInstance(
        application.applicationContext,
        Executors.newSingleThreadExecutor()
    )
    private val messageRepository = MessageRepository.getInstance(
        Executors.newSingleThreadExecutor()
    )

    lateinit var user: User
    lateinit var conversationDetailList: LiveData<List<Chat>>
    fun getConversationDetail(uid: String) {
        conversationDetailList = chatRepository.getConversationDetails(uid)
    }

    fun sendChat(message: String) {
        Log.i(TAG, "sendChat -> $message")
        val localChat = Chat(
            UUID.randomUUID().toString(),
            user.uid,
            ChatDirection.OutGoing,
            true,
            ChatType.SEND_TEXT,
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
            ChatType.RECEIVED_TEXT,
            message,
            null,
            localChat.createDate,
            null
        )

        if (!user.token.isNullOrEmpty())
            messageRepository.sendChat(user.token.toString(), remoteChat)
    }

    fun sendImage(fileName: String, messageUri: String, chatType: ChatType) {
        val localChat = Chat(
            UUID.randomUUID().toString(),
            user.uid,
            ChatDirection.OutGoing,
            true,
                chatType,
            getFileNameWithUserUid(fileName),
            messageUri,
            Date().time,
            null
        )
        chatRepository.addChat(localChat)

        val remoteChat = Chat(
            localChat.uid,
            firebaseDao.getCurrentUserUid(),
            ChatDirection.InComing,
            false,
            getRemoteMediaType(chatType),
            localChat.text,
            messageUri,
            localChat.createDate,
            null
        )

        if (!user.token.isNullOrEmpty())
            messageRepository.sendChat(user.token.toString(), remoteChat)
    }

    fun getRemoteMediaType(chatType: ChatType): ChatType {
        return when (chatType) {
            ChatType.SEND_IMAGE -> ChatType.RECEIVED_IMAGE
            else -> ChatType.RECEIVED_VIDEO
        }
    }

    fun removeChatAtPosition(position: Int) {
        chatRepository.removeChat(conversationDetailList.value!![position])
    }

    fun getChatDirection(position: Int): ChatDirection =
        conversationDetailList.value!![position].direction

    fun sendRemoveRequestAtPosition(position: Int) {
        messageRepository.removeChat(
            user.token.toString(),
            conversationDetailList.value!![position]
        )
    }

    fun markAsRead() {
        chatRepository.markAsRead(user.uid)
    }

    fun getFileNameWithUserUid(fileName: String): String {
        return firebaseDao.getCurrentUserUid() + "_" + fileName
    }
}
