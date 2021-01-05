package com.haksoy.voipapp.ui.discover

import User
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.haksoy.voipapp.data.FirebaseDao

class MapsViewModel : ViewModel() {

    val firebaseDao = FirebaseDao.getInstance()

    val nearlyUsers = MutableLiveData<List<User>>()

    fun fetchNearlyUsers() {
        firebaseDao.getLocation(firebaseDao.getCurrentUserUid()).observeForever {
            nearlyUsers.postValue(firebaseDao.getNearlyUsers(it))
        }
    }

}