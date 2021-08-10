package com.haksoy.soip.ui.gallery

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.haksoy.soip.data.database.ChatRepository
import java.util.concurrent.Executors

class MediaGalleryViewModel(application: Application) : AndroidViewModel(application) {
    private val chatRepository = ChatRepository.getInstance(
        application.applicationContext,
        Executors.newSingleThreadExecutor()
    )

    fun getConversationMedia(uid: String) = chatRepository.getConversationMedia(uid)
}