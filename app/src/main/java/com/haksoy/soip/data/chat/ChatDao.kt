package com.haksoy.soip.data.chat

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface ChatDao {
    @Query("SELECT * FROM chat_table WHERE userUid=(:uid) ORDER BY createDate DESC")
    fun getConversationDetails(uid: String): LiveData<List<Chat>>

    @Insert
    fun addChat(chatItem: Chat)
}