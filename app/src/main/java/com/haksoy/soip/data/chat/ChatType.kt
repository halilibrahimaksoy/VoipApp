package com.haksoy.soip.data.chat

import com.haksoy.soip.R

enum class ChatType {
    SEND_TEXT,
    RECEIVED_TEXT,
    SEND_IMAGE,
    RECEIVED_IMAGE,
    SEND_VIDEO,
    TEXT,
    RECEIVED_VIDEO;

    companion object {
         fun isMedia(type: ChatType): Boolean {
            return !(type == ChatType.SEND_TEXT || type == ChatType.RECEIVED_TEXT)
        }

         fun getDrawableId(type: ChatType): Int {
            return when (type) {
                SEND_IMAGE,
                RECEIVED_IMAGE -> R.drawable.ic_photo_camera_black_48dp
                SEND_VIDEO,
                RECEIVED_VIDEO -> R.drawable.ic_camera
                else -> -1;
            }
        }
        fun getMessageId(type: ChatType):Int {
            return when (type) {
                SEND_IMAGE,
                RECEIVED_IMAGE -> R.string.image
                SEND_VIDEO,
                RECEIVED_VIDEO -> R.string.video
                else -> -1;
            }
        }
    }
}
