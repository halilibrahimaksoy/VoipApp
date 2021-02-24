package com.haksoy.soip.ui.main

import com.haksoy.soip.data.entiries.User
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.haksoy.soip.data.FirebaseDao
import com.haksoy.soip.utlis.Resource

private const val TAG = "SoIP:SharedViewModel"

class SharedViewModel : ViewModel() {

    private val firebaseDao = FirebaseDao.getInstance()

    val nearlyUsers = MutableLiveData<List<User>>()
    var selectedUserList = ArrayList<User>()
    val selectedUserUid = MutableLiveData<String>()
    val selectedUser = MutableLiveData<User>()
    val conversationDetailWithUser = MutableLiveData<User>()

    fun getPositionFromUid(): Int {
        for (i in selectedUserList.indices) {
            if (selectedUserList[i].uid == selectedUserUid.value)
                return selectedUserList.size * ((Int.MAX_VALUE / selectedUserList.size) / 2) + i
        }
        return -1
    }

    fun fetchNearlyUsers() {
        firebaseDao.getLocation(firebaseDao.getCurrentUserUid())
        firebaseDao.currentLocation.observeForever {
                Log.i(TAG, "fetchNearlyUsers: Location Observed")
                firebaseDao.getNearlyUsers(it);
                firebaseDao.nearlyUser.observeForever {
                    if (it.status == Resource.Status.SUCCESS) {
                        Log.i(TAG, "fetchNearlyUsers: postValue data")
                        nearlyUsers.postValue(it.data!!)
                    } else if (it.status == Resource.Status.ERROR) {

                }
            }
        }
    }
}