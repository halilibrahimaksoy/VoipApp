package com.haksoy.soip.messaging

import android.util.Log
import com.haksoy.soip.data.FirebaseDao
import com.haksoy.soip.data.chat.Chat
import com.haksoy.soip.data.chat.ChatType
import com.haksoy.soip.data.message.*
import com.haksoy.soip.utlis.Resource
import com.haksoy.soip.utlis.observeOnce
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.concurrent.ExecutorService
private const val TAG = "SoIP:MessageRepository"

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
    private val firebaseDao = FirebaseDao.getInstance()

    fun sendChat(to: String, remoteChat: Chat) {
        if (ChatType.isMedia(remoteChat.type)) {
            firebaseDao.uploadMedia(remoteChat.text!!, remoteChat.contentUrl!!).observeOnce {
                if (it.status == Resource.Status.SUCCESS) {
                    Log.i(TAG, "uploadMedia SUCCESS")
                    remoteChat.contentUrl = it.data
                    sendMessage(to, remoteChat)
                } else if (it.status == Resource.Status.ERROR) {
                    //todo implement fail scenerio
                }
            }
        } else {
            sendMessage(to, remoteChat)
        }
    }

    private fun sendMessage(to: String, remoteChat: Chat) {
        Log.i(TAG, "sendMessage -> $remoteChat to -> $to ")
        firebaseAPIService.create(FirebaseAPIService::class.java).sendNotification(
                MessageBody(
                        to,
                        MessageData(
                                MessageEventType.CHAT,
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
        Log.i(TAG, "removeChat -> $remoteChat to -> $to ")
        firebaseAPIService.create(FirebaseAPIService::class.java).sendNotification(
                MessageBody(
                        to,
                        MessageData(
                                MessageEventType.CHAT,
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