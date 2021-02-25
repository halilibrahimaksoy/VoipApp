package com.haksoy.soip.data.chat

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query


@Dao
interface ConversationDao {
    @Query("SELECT * FROM latest_chat_table ORDER BY createDate DESC")
    fun getConversationList(): LiveData<List<Conversation>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun addConversation(conversation: Conversation)
}