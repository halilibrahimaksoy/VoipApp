package com.haksoy.soip.data.notification

data class MessageData(val event: EventType, val content: Any) {
}

enum class EventType {
    CHAT, INFORMATION
}