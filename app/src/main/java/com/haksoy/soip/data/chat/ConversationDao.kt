package com.haksoy.soip.data.chat

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

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

    @Query("UPDATE conversation_table SET is_seen=1 WHERE userUid=(:userUid)")
    fun marAsRead(userUid: String)
}