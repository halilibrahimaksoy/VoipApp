package com.haksoy.soip.data.chat

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface ConversationDao {
    @Query("SELECT * FROM conversation_table ORDER BY createDate DESC")
    fun getConversationList(): LiveData<List<Conversation>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun addConversation(conversation: Conversation)

    @Query("DELETE FROM conversation_table WHERE userUid=(:userUid)")
    fun removeConversation(userUid: String)

    @Query("UPDATE conversation_table SET text = null WHERE chatUid=(:chatUid)")
    fun removeChat(chatUid: String)
}