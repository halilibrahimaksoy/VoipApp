package com.haksoy.soip.ui.conversationList

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.haksoy.soip.data.chat.Conversation
import com.haksoy.soip.data.database.ChatRepository
import com.haksoy.soip.data.database.UserRepository
import com.haksoy.soip.data.user.User
import com.haksoy.soip.utlis.Resource
import com.haksoy.soip.utlis.observeOnce
import java.util.concurrent.Executors

class ConversationListViewModel(application: Application) : AndroidViewModel(application) {
    private val chatRepository = ChatRepository.getInstance(
        application.applicationContext,
        Executors.newSingleThreadExecutor()
    )
    private val userRepository = UserRepository.getInstance(
        application.applicationContext,
        Executors.newSingleThreadExecutor()
    )
    val conversationWithUserLiveData = getConversationList()

    private fun getConversationList(): MutableLiveData<LinkedHashMap<User, Conversation>> {
        val result = MutableLiveData<LinkedHashMap<User, Conversation>>()
        val conversationsWithUser = LinkedHashMap<User, Conversation>()
        chatRepository.getConversationList().observeForever { conversations ->
            conversationsWithUser.clear()
            for (chat in conversations) {
                if (!chat.is_seen)
                    chatRepository.getUnreadMessageCount(chat.userUid).observeOnce {
                        chat.unread_message_count = it
                    }
                userRepository.getUser(chat.userUid).observeOnce {
                    if (it.status == Resource.Status.SUCCESS) {
                        conversationsWithUser[it.data as User] = chat
                        result.postValue(conversationsWithUser)
                    }
                }
            }
        }
        return result
    }

    fun removeConversationAtPosition(position: Int) {
        chatRepository.removeConversation(conversationWithUserLiveData.value!!.keys.toList()[position].uid)
    }

    fun markAsReadConversation(userUid: String) = chatRepository.marAsRead(userUid)
}
