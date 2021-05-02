package com.haksoy.soip.messaging

import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.haksoy.soip.data.FirebaseDao
import com.haksoy.soip.data.message.MessageChat
import com.haksoy.soip.data.message.MessageEventType
import com.haksoy.soip.utlis.Constants
import com.haksoy.soip.utlis.JsonUtil
import com.haksoy.soip.utlis.putPreferencesString


class MyFirebaseMessagingService : FirebaseMessagingService() {
    private lateinit var messageHandler: MessageHandler
    override fun onMessageReceived(p0: RemoteMessage) {
        super.onMessageReceived(p0)
        messageHandler = MessageHandler(this)

        if (p0.data.containsKey(Constants.MESSAGE_EVENT_TYPE)) {
            val messageData = JsonUtil.getMessageData(p0.data.toString())

            when (messageData.messageEventType) {
                MessageEventType.CHAT -> {
                    messageHandler.handleChatNotificationData(messageData.content as MessageChat)
                }
            }
        }
    }


    override fun onNewToken(p0: String) {
        super.onNewToken(p0)
        if (FirebaseDao.getInstance().isAuthUserExist())
            FirebaseDao.getInstance().updateToken(p0)
        putPreferencesString(Constants.FIREBASE_MESSAGING_TOKEN, p0)
    }

    companion object {

        private const val TAG = "MyFirebaseMsgService"
    }
}