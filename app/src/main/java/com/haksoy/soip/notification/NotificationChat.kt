package com.haksoy.soip.notification

import com.haksoy.soip.data.chat.Chat

data class NotificationChat(val chatType: NotificationChatType, val chat: Chat) {
}

enum class NotificationChatType {
    NEW, DELETE
}