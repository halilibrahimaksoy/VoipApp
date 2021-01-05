package com.haksoy.voipapp.ui.profile

import User
import android.net.Uri
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import com.haksoy.voipapp.data.FirebaseDao
import com.haksoy.voipapp.utlis.Resource

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
        firebaseDao.fetchUserDate(uid).observeForever(Observer {
            if (it.status == Resource.Status.SUCCESS) {
                currentUser.postValue(it.data)
            } else if (it.status == Resource.Status.ERROR) {

            }
        })
    }

    fun updateUserProfile(
        newImageUri: Uri? = null,
        name: String,
        info: String
    ): MutableLiveData<Resource<Exception>> {
        return firebaseDao.updateUserProfile(newImageUri, name, info)
    }


}