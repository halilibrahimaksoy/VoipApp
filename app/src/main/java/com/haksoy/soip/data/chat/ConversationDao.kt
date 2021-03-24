package com.haksoy.soip.data.chat

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface ConversationDao {
    @Query("SELECT * FROM conversation_table ORDER BY createDate DESC")
    fun getConversationList(): LiveData<List<Conversation>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun addConversation(conversation: Conversation)

    @Delete
    fun removeConversation(conversation: Conversation)
}