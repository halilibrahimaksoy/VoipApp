package com.haksoy.soip.notification

import com.haksoy.soip.data.chat.Chat
import com.haksoy.soip.data.notification.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.concurrent.ExecutorService

class MessageRepository(private val executor: ExecutorService) {
    companion object {
        @Volatile
        private var INSTANCE: MessageRepository? = null

        fun getInstance(executor: ExecutorService): MessageRepository {
            return INSTANCE
                ?: synchronized(this) {
                    INSTANCE
                        ?: MessageRepository(
                            executor
                        )
                            .also { INSTANCE = it }
                }
        }
    }

    private val firebaseAPIService = RetrofitService.getService()

    fun sendChat(to: String, remoteChat: Chat) {
        firebaseAPIService.create(FirebaseAPIService::class.java).sendNotification(
            MessageBody(
                to,
                MessageData(
                    EventType.CHAT,
                    MessageChat(
                        ChatEventType.NEW,
                        remoteChat
                    )
                )
            )
        ).enqueue(object : Callback<MessageResponse> {
            override fun onFailure(call: Call<MessageResponse>, t: Throwable) {
                println("")
            }

            override fun onResponse(
                    call: Call<MessageResponse>,
                    response: Response<MessageResponse>
            ) {
                println("")
            }

        })
    }

    fun removeChat(to: String, remoteChat: Chat) {
        firebaseAPIService.create(FirebaseAPIService::class.java).sendNotification(
            MessageBody(
                to,
                MessageData(
                    EventType.CHAT,
                    MessageChat(
                        ChatEventType.DELETE,
                        remoteChat
                    )
                )
            )
        ).enqueue(object : Callback<MessageResponse> {
            override fun onFailure(call: Call<MessageResponse>, t: Throwable) {
                println("")
            }

            override fun onResponse(
                    call: Call<MessageResponse>,
                    response: Response<MessageResponse>
            ) {
                println("")
            }

        })
    }
}