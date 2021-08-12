package com.haksoy.soip.data.database

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import com.haksoy.soip.data.chat.Chat
import com.haksoy.soip.data.chat.Conversation
import java.util.concurrent.ExecutorService

private const val TAG = "SoIP:ChatRepository"
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

    fun getUnreadMessageCount(userUid: String): LiveData<Int> =
        chatDao.getUnreadMessageCount(userUid)

    fun getConversationDetails(uid: String): LiveData<List<Chat>> =
        chatDao.getConversationDetails(uid)

    fun getConversationMedia(uid: String): LiveData<List<Chat>> =
        chatDao.getConversationMedia(uid)


    fun getUnreadConversation(uid: String): LiveData<List<Chat>> =
        chatDao.getUnreadConversation(uid)

    fun addChat(chatItem: Chat) {
        Log.i(TAG,"addChat -> $chatItem")
        executor.execute {
            chatDao.addChat(chatItem)
            conversationDao.addConversation(
                Conversation(
                    chatItem.uid,
                    chatItem.userUid,
                    chatItem.direction,
                    chatItem.is_seen,
                    0,
                    chatItem.type,
                    chatItem.getText(),
                    chatItem.createDate
                )
            )
        }
    }

    fun removeChat(chatItem: Chat) {
        Log.i(TAG,"removeChat -> $chatItem")
        executor.execute {
            chatDao.removeChat(chatItem)
            conversationDao.removeChat(chatItem.uid)
        }
    }

    fun removeConversation(userUid: String) {
        Log.i(TAG,"removeConversation -> $userUid")
        executor.execute {
            chatDao.removeConversation(userUid)
            conversationDao.removeConversation(userUid)
        }
    }

    fun markAsRead(userUid: String) {
        Log.i(TAG,"markAsRead -> $userUid")
        executor.execute {
            chatDao.markAsRead(userUid)
            conversationDao.markAsRead(userUid)
        }
    }
}