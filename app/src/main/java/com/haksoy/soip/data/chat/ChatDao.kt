package com.haksoy.soip.data.chat

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface ChatDao {
    @Query("SELECT * FROM chat_table WHERE userUid=(:uid) ORDER BY createDate DESC")
    fun getConversationDetails(uid: String): LiveData<List<Chat>>

    @Insert
    fun addChat(chatItem: Chat)

    @Delete
    fun removeChat(chatItem: Chat)

    @Query("DELETE FROM chat_table WHERE userUid=(:userUid)")
    fun removeConversation(userUid: String)

    @Query("SELECT count(*) FROM chat_table WHERE userUid=(:userUid) and is_seen=0")
    fun getUnreadMessageCount(userUid: String): LiveData<Int>

    @Query("SELECT * FROM chat_table WHERE userUid=(:userUid) and is_seen=0 ORDER BY createDate ASC")
    fun getUnreadConversation(userUid: String): LiveData<List<Chat>>

    @Query("UPDATE chat_table SET is_seen=1 WHERE userUid=(:userUid) and is_seen=0")
    fun marAsRead(userUid: String)
}