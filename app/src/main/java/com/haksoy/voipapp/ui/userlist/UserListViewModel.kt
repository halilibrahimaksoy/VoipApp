package com.haksoy.voipapp.ui.userlist

import User
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.haksoy.voipapp.data.FirebaseDao

class UserListViewModel : ViewModel() {

    private val firebaseDao = FirebaseDao.getInstance()

    val nearlyUsers = MutableLiveData<List<User>>()
    val selectedUserUid = MutableLiveData<String>()
    val selectedUser = MutableLiveData<User>()

    fun getPositionFromUid(): Int {
        for (i in nearlyUsers.value!!.indices) {
            if (nearlyUsers.value!![i].uid == selectedUserUid.value)
                return Int.MAX_VALUE / 2 + i
        }
        return -1
    }

    fun fetchNearlyUsers() {
        firebaseDao.getLocation(firebaseDao.getCurrentUserUid()).observeForever {
            nearlyUsers.postValue(firebaseDao.getNearlyUsers(it))
        }
    }
}