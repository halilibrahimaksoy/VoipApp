package com.haksoy.soip.ui.profile

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.haksoy.soip.data.user.User
import androidx.lifecycle.MutableLiveData
import com.haksoy.soip.data.FirebaseDao
import com.haksoy.soip.data.database.UserRepository
import com.haksoy.soip.utlis.Resource
import com.haksoy.soip.utlis.observeOnce
import java.util.concurrent.Executors

class UserProfileViewModel(application: Application) : AndroidViewModel(application) {
    val currentUser = MutableLiveData<User>()

    private val firebaseDao = FirebaseDao.getInstance()
    private val userRepository = UserRepository.getInstance(
            application.applicationContext,
            Executors.newSingleThreadExecutor()
    )
    fun getUid(): String {
        return firebaseDao.getCurrentUserUid()
    }

    fun getEmail(): String {
        return firebaseDao.getCurrentUserEmail()
    }

    fun fetchUserDate(uid: String) {
        firebaseDao.fetchUserDate(uid).observeOnce {
            if (it.status == Resource.Status.SUCCESS) {
                currentUser.postValue(it.data!!)
            } else if (it.status == Resource.Status.ERROR) {

            }
        }
    }
    fun addUser(user: User){
        userRepository.addUser(user)
    }

    fun updateUserProfile(user: User): MutableLiveData<Resource<Exception>> {
        return firebaseDao.updateUserProfile(user)
    }


}