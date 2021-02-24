package com.haksoy.soip.ui.conversationList

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.haksoy.soip.chat.Chat
import com.haksoy.soip.chat.ChatRepository
import com.haksoy.soip.data.FirebaseDao
import com.haksoy.soip.data.entiries.User
import com.haksoy.soip.utlis.observeOnce
import java.util.concurrent.Executors

class ConversationListViewModel(application: Application) : AndroidViewModel(application) {
    private val firebaseDao = FirebaseDao.getInstance()
    private val chatRepository = ChatRepository.getInstance(
        application.applicationContext,
        Executors.newSingleThreadExecutor()
    )
    val conversationWithUserLiveData = getConversationList()

    private fun getConversationList(): MutableLiveData<LinkedHashMap<User, Chat>> {
        val result = MutableLiveData<LinkedHashMap<User, Chat>>()
        val conversationsWithUser = LinkedHashMap<User, Chat>()
        chatRepository.getConversationList().observeForever { conversations ->
            conversationsWithUser.clear()
            for (chat in conversations) {
                firebaseDao.fetchUserDate(chat.userUid).observeOnce {

                    it.data?.let { it1 -> conversationsWithUser[it1] = chat
                        result.postValue(conversationsWithUser)
                    }
                }
            }

        }
        return result
    }
}
