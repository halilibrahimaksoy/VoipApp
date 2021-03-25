package com.haksoy.soip.ui.conversationDetail

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.haksoy.soip.data.FirebaseDao
import com.haksoy.soip.data.chat.Chat
import com.haksoy.soip.data.chat.ChatDirection
import com.haksoy.soip.data.chat.ChatType
import com.haksoy.soip.data.database.ChatRepository
import com.haksoy.soip.data.user.User
import com.haksoy.soip.notification.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*
import java.util.concurrent.Executors

class ConversationDetailViewModel(application: Application) : AndroidViewModel(application) {
    private val firebaseDao = FirebaseDao.getInstance()
    private val chatRepository = ChatRepository.getInstance(
        application.applicationContext,
        Executors.newSingleThreadExecutor()
    )

    lateinit var user: User
    fun getConversationDetail(uid: String): LiveData<List<Chat>> =
        chatRepository.getConversationDetails(uid)


    fun sendChat(message: String) {

        val localChat = Chat(
            UUID.randomUUID(),
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
        RetrofitService.getService().create(FirebaseAPIService::class.java).sendNotification(
            NotificationBody(
                user.token.toString(),
                NotificationData(
                    NotificationType.CHAT,
                    NotificationChat(NotificationChatType.NEW, remoteChat)
                )
            )
        ).enqueue(object : Callback<NotificationResponse> {
            override fun onFailure(call: Call<NotificationResponse>, t: Throwable) {
                println("")
            }

            override fun onResponse(
                call: Call<NotificationResponse>,
                response: Response<NotificationResponse>
            ) {
                println("")
            }

        })
    }
}
