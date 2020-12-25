package com.haksoy.voipapp.ui.userlist

import User
import UserList
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.haksoy.voipapp.data.UserRepository
import com.haksoy.voipapp.data.remote.OperationCallback

class UserListViewModel : ViewModel() {


    val userRepository: UserRepository = UserRepository()
    private val _users = MutableLiveData<List<User>>().apply { value = emptyList() }
    val mUsers: LiveData<List<User>> = _users
    fun loadUsers() {

        userRepository.fetchUsers(100, object : OperationCallback<UserList> {
            override fun onSuccess(data: List<Any>?) {
                _users.value = data as List<User>?
            }

            override fun onError(error: String?) {
                println("asdfasdf")
            }

        })
    }

}