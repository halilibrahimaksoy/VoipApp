package com.haksoy.voipapp.ui.profile

import User
import android.net.Uri
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.haksoy.voipapp.data.FirebaseDao
import com.haksoy.voipapp.utlis.Resource
import com.haksoy.voipapp.utlis.observeOnce

class UserProfileViewModel : ViewModel() {
    val currentUser = MutableLiveData<User>()

    private val firebaseDao = FirebaseDao.getInstance()
    fun getUid(): String {
        return firebaseDao.getCurrentUserUid()
    }

    fun getEmail(): String {
        return firebaseDao.getCurrentUserEmail()
    }

    fun fetchUserDate(uid: String) {
        firebaseDao.fetchUserDate(uid).observeOnce {
            if (it.status == Resource.Status.SUCCESS) {
                currentUser.postValue(it.data)
            } else if (it.status == Resource.Status.ERROR) {

            }
        }
    }

    fun updateUserProfile(user: User): MutableLiveData<Resource<Exception>> {
        return firebaseDao.updateUserProfile(user)
    }


}