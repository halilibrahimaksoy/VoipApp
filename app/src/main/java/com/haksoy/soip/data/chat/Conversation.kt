package com.haksoy.soip.data.chat

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.haksoy.soip.utlis.Constants
import java.util.*

@Entity(tableName = Constants.CONVERSATION_TABLE)
data class Conversation(
    val chatUid: String = UUID.randomUUID().toString(),
    @PrimaryKey val userUid: String,
    val direction: ChatDirection,
    val is_seen: Boolean,
    val type: ChatType,
    val text: String? = null,
    val createDate: Long
) {

    override fun toString(): String {
        return if (direction == ChatDirection.InComing) "$text from  $userUid" else "$text to  $userUid"
    }

}