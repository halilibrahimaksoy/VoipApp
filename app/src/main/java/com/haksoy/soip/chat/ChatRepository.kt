package com.haksoy.soip.chat

import android.content.Context
import androidx.lifecycle.LiveData
import java.util.concurrent.ExecutorService

class ChatRepository private constructor(
    chatDatabase: ChatDatabase,
    private val executor: ExecutorService
) {

    companion object {
        @Volatile
        private var INSTANCE: ChatRepository? = null

        fun getInstance(context: Context, executor: ExecutorService): ChatRepository {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: ChatRepository(
                    ChatDatabase.getInstance(context),
                    executor
                )
                    .also { INSTANCE = it }
            }
        }
    }

    private val chatDao = chatDatabase.chatDao()


    fun getConversationList(): LiveData<List<Chat>> = chatDao.getConversationList()

    fun getConversationDetails(uid: String): LiveData<List<Chat>> = chatDao.getConversationDetails(uid)

    fun addChat(chatItem: Chat) {
        executor.execute {
            chatDao.addChat(chatItem)
        }
    }
}