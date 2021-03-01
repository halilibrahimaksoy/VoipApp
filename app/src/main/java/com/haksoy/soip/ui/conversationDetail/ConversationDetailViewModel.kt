package com.haksoy.soip.ui.conversationDetail

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.haksoy.soip.data.chat.Chat
import com.haksoy.soip.data.database.ChatRepository
import com.haksoy.soip.data.FirebaseDao
import java.util.concurrent.Executors

class ConversationDetailViewModel(application: Application) : AndroidViewModel(application) {
    private val firebaseDao = FirebaseDao.getInstance()
    private val chatRepository = ChatRepository.getInstance(
            application.applicationContext,
            Executors.newSingleThreadExecutor()
    )

    fun getConversationDetail(uid: String): LiveData<List<Chat>> = chatRepository.getConversationDetails(uid)


    fun sendChat(chat: Chat) {
        chatRepository.addChat(chat)
    }
}
