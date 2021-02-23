package com.haksoy.soip.ui.chat

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.haksoy.soip.chat.Chat
import com.haksoy.soip.chat.ChatRepository
import com.haksoy.soip.data.FirebaseDao
import com.haksoy.soip.data.entiries.User
import com.haksoy.soip.utlis.observeOnce
import java.util.concurrent.Executors

class ChatListViewModel(application: Application) : AndroidViewModel(application) {
    private val firebaseDao = FirebaseDao.getInstance()
    private val chatRepository = ChatRepository.getInstance(
            application.applicationContext,
            Executors.newSingleThreadExecutor()
    )
    val chatListWithUserLiveData = getChatListWithUser()

    private fun getChatListWithUser(): MutableLiveData<LinkedHashMap<User, Chat>> {
        val result = MutableLiveData<LinkedHashMap<User, Chat>>()
        val chatListWithUser = LinkedHashMap<User, Chat>()
        chatRepository.getAllChatList().observeForever { chatList ->
            for (chat in chatList) {
                firebaseDao.fetchUserDate(chat.userUid).observeOnce {
                    it.data?.let { it1 -> chatListWithUser.put(it1, chat) }
                }
            }
            result.postValue(chatListWithUser)
        }
        return result
    }
}
