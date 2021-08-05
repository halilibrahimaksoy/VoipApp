package com.haksoy.soip.ui.conversationList

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import com.haksoy.soip.data.chat.Conversation
import com.haksoy.soip.data.database.ChatRepository
import com.haksoy.soip.data.database.UserRepository
import com.haksoy.soip.data.user.User
import com.haksoy.soip.utlis.Resource
import com.haksoy.soip.utlis.observeOnce
import java.util.*
import java.util.concurrent.Executors
import kotlin.collections.LinkedHashMap

private const val TAG = "SoIP:ConversationListViewModel"

class ConversationListViewModel(application: Application) : AndroidViewModel(application) {
    private val chatRepository = ChatRepository.getInstance(
            application.applicationContext,
            Executors.newSingleThreadExecutor()
    )
    private val userRepository = UserRepository.getInstance(
            application.applicationContext,
            Executors.newSingleThreadExecutor()
    )

    val conversationWithUserLiveData: LiveData<LinkedHashMap<User, Conversation>>
        get() = Transformations.switchMap(filterNameForCLF) { searchKey ->
            val conversationWithUser = getConversationList()
            val result = when (searchKey) {
                null,
                "" -> conversationWithUser
                else -> {
                    Transformations.switchMap(conversationWithUser) { linkedHashMap ->
                        val filteredResult = MutableLiveData<LinkedHashMap<User, Conversation>>()
                        val filteredList = linkedHashMap.filter {
                            it.key.name!!.toLowerCase(Locale.getDefault()).contains(searchKey.trim().toLowerCase(Locale.getDefault()))
                        }
                        filteredResult.value = LinkedHashMap(filteredList)
                        filteredResult
                    }
                }
            }
            result
        }

    private fun getConversationList(): MutableLiveData<LinkedHashMap<User, Conversation>> {
        val result = MutableLiveData<LinkedHashMap<User, Conversation>>()
        val conversationsWithUser = LinkedHashMap<User, Conversation>()
        chatRepository.getConversationList().observeForever { conversations ->
            Log.i(TAG, "getConversationList: Conversations Observed $(conversations.size)")
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
        Log.i(TAG, "removeConversationAtPosition: position at $position")
        conversationWithUserLiveData.observeOnce {
            chatRepository.removeConversation(it.keys.toList()[position].uid)
        }
    }

    val filterNameForCLF = MutableLiveData<String>("")
}
