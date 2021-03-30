package com.haksoy.soip.data.chat

import androidx.room.TypeConverter
import java.util.*

class ChatTypeConverters {
    @TypeConverter
    fun fromDate(date: Date?): Long? {
        return date?.time
    }

    @TypeConverter
    fun toDate(millisSinceEpoch: Long?): Date? {
        return millisSinceEpoch?.let {
            Date(it)
        }
    }

    @TypeConverter
    fun toChatType(value: String) = enumValueOf<ChatType>(value)

    @TypeConverter
    fun fromChatType(value: ChatType) = value.name

    @TypeConverter
    fun toChatDirection(value: String) = enumValueOf<ChatDirection>(value)

    @TypeConverter
    fun fromChatDirection(value: ChatDirection) = value.name
}