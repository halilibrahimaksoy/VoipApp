package com.haksoy.soip.data.message

data class MessageData(val event: EventType, val content: Any) {
}

enum class EventType {
    CHAT, INFORMATION
}