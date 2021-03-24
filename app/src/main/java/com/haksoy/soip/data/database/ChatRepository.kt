package com.haksoy.soip.data.database

import android.content.Context
import androidx.lifecycle.LiveData
import com.haksoy.soip.data.chat.Chat
import com.haksoy.soip.data.chat.Conversation
import java.util.concurrent.ExecutorService

class ChatRepository private constructor(
    chatDatabase: ChatDatabase,
    private val executor: ExecutorService
) {

    companion object {
        @Volatile
        private var INSTANCE: ChatRepository? = null

        fun getInstance(context: Context, executor: ExecutorService): ChatRepository {
            return INSTANCE
                ?: synchronized(this) {
                    INSTANCE
                        ?: ChatRepository(
                            ChatDatabase.getInstance(context),
                            executor
                        )
                            .also { INSTANCE = it }
                }
        }
    }

    private val chatDao = chatDatabase.chatDao()
    private val conversationDao = chatDatabase.conversationDao()


    fun getConversationList(): LiveData<List<Conversation>> = conversationDao.getConversationList()

    fun getConversationDetails(uid: String): LiveData<List<Chat>> =
        chatDao.getConversationDetails(uid)

    fun addChat(chatItem: Chat) {
        executor.execute {
            chatDao.addChat(chatItem)
            conversationDao.addConversation(
                Conversation(
                    chatItem.uid,
                    chatItem.userUid,
                    chatItem.direction,
                    chatItem.is_seen,
                    chatItem.type,
                    chatItem.text,
                    chatItem.createDate
                )
            )
        }
    }

    fun removeChat(chatItem: Chat) {
        executor.execute {
            chatDao.removeChat(chatItem)
        }
    }
}