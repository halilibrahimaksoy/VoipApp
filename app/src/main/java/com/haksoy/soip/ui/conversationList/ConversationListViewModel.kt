package com.haksoy.soip.ui.conversationList

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.haksoy.soip.data.FirebaseDao
import com.haksoy.soip.data.chat.Conversation
import com.haksoy.soip.data.database.ChatRepository
import com.haksoy.soip.data.database.UserRepository
import com.haksoy.soip.data.user.User
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
                userRepository.getUser(chat.userUid).observeOnce {
                    conversationsWithUser[it] = chat
                    result.postValue(conversationsWithUser)
                }
            }
        }
        return result
    }

    fun removeConversationAtPosition( position:Int){
        chatRepository.removeConversation(conversationWithUserLiveData.value!!.keys.toList()[position].uid)
    }
}
