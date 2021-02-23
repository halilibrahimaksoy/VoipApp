package com.haksoy.soip.chat

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.haksoy.soip.utlis.Constants
import java.util.*

@Entity(tableName = Constants.CHAT_TABLE)
data class Chat(
        @PrimaryKey val uid: UUID = UUID.randomUUID(),
        val userUid: String,
        val direction: ChatDirection,
        val is_seen: Boolean,
        val type: ChatType,
        val text: String,
        val contentUrl: String,
        val createDate: Date,
        val updateDate: Date
) {

    override fun toString(): String {
        return if (direction == ChatDirection.InComing) "$text$contentUrl from  $userUid" else "$text$contentUrl to  $userUid"
    }

}

enum class ChatType {
    TEXT, IMAGE, CONTACT
}

enum class ChatDirection {
    InComing, OutGoing
}
