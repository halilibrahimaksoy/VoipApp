package com.haksoy.soip.ui.userlist

import User
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.haksoy.soip.data.FirebaseDao

private const val TAG = "SoIP:UserListViewModel"
class UserListViewModel : ViewModel() {

    private val firebaseDao = FirebaseDao.getInstance()

    val nearlyUsers = MutableLiveData<List<User>>()
    var selectedUserList = ArrayList<User>()
    val selectedUserUid = MutableLiveData<String>()
    val selectedUser = MutableLiveData<User>()

    fun getPositionFromUid(): Int {
        for (i in selectedUserList.indices) {
            if (selectedUserList[i].uid == selectedUserUid.value)
                return Int.MAX_VALUE / 2 + i
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