package com.haksoy.soip.data.notification

data class NotificationData(val notificationType: NotificationType, val content: Any) {
}

enum class NotificationType {
    CHAT, INFORMATION
}