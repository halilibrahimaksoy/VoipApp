package com.haksoy.soip.notification

import com.haksoy.soip.data.chat.Chat
import com.haksoy.soip.data.notification.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.concurrent.ExecutorService

class NotificationRepository(private val executor: ExecutorService) {
    companion object {
        @Volatile
        private var INSTANCE: NotificationRepository? = null

        fun getInstance(executor: ExecutorService): NotificationRepository {
            return INSTANCE
                ?: synchronized(this) {
                    INSTANCE
                        ?: NotificationRepository(
                            executor
                        )
                            .also { INSTANCE = it }
                }
        }
    }

    private val firebaseAPIService = RetrofitService.getService()

    fun sendChat(to: String, remoteChat: Chat) {
        firebaseAPIService.create(FirebaseAPIService::class.java).sendNotification(
            NotificationBody(
                to,
                NotificationData(
                    NotificationType.CHAT,
                    NotificationChat(
                        NotificationChatType.NEW,
                        remoteChat
                    )
                )
            )
        ).enqueue(object : Callback<NotificationResponse> {
            override fun onFailure(call: Call<NotificationResponse>, t: Throwable) {
                println("")
            }

            override fun onResponse(
                call: Call<NotificationResponse>,
                response: Response<NotificationResponse>
            ) {
                println("")
            }

        })
    }

    fun removeChat(to: String, remoteChat: Chat) {
        firebaseAPIService.create(FirebaseAPIService::class.java).sendNotification(
            NotificationBody(
                to,
                NotificationData(
                    NotificationType.CHAT,
                    NotificationChat(
                        NotificationChatType.DELETE,
                        remoteChat
                    )
                )
            )
        ).enqueue(object : Callback<NotificationResponse> {
            override fun onFailure(call: Call<NotificationResponse>, t: Throwable) {
                println("")
            }

            override fun onResponse(
                call: Call<NotificationResponse>,
                response: Response<NotificationResponse>
            ) {
                println("")
            }

        })
    }
}