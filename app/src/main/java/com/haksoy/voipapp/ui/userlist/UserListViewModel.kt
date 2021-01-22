package com.haksoy.voipapp.ui.userlist

import User
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.haksoy.voipapp.data.FirebaseDao

private const val TAG = "UserListViewModel"
class UserListViewModel : ViewModel() {

    private val firebaseDao = FirebaseDao.getInstance()

    val nearlyUsers = MutableLiveData<List<User>>()
    val selectedUserList = MutableLiveData<List<User>>()
    val selectedUserUid = MutableLiveData<String>()
    val selectedUser = MutableLiveData<User>()

    fun getPositionFromUid(): Int {
        for (i in selectedUserList.value!!.indices) {
            if (selectedUserList.value!![i].uid == selectedUserUid.value)
                return Int.MAX_VALUE / 2 + i

            if (i == 19)
                return -1
        }
        return -1
    }

    fun fetchNearlyUsers() {
        firebaseDao.getLocation(firebaseDao.getCurrentUserUid()).observeForever {
            Log.i(TAG,"userListViewModel  :  nearlyUsers posted new value")
            nearlyUsers.postValue(firebaseDao.getNearlyUsers(it))
        }
    }
}