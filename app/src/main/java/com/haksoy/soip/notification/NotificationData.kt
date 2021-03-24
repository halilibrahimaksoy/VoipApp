package com.haksoy.soip.notification

data class NotificationData(val notificationType: NotificationType, val content: Any) {
}

enum class NotificationType {
    CHAT, INFORMATION
}