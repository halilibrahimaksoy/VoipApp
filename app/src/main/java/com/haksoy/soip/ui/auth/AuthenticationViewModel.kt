package com.haksoy.soip.ui.auth

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.haksoy.soip.data.FirebaseDao
import com.haksoy.soip.utlis.Resource

class AuthenticationViewModel : ViewModel() {
    val firebaseDao = FirebaseDao.getInstance()

    fun createAccount(email: String, password: String): LiveData<Resource<Exception>> {
        return firebaseDao.createAccount(email, password)
    }

    fun signIn(email: String, password: String): LiveData<Resource<Exception>> {
        return  firebaseDao.signIn(email, password)
    }

    fun forgetPassword(email: String): LiveData<Resource<Exception>> {
        return firebaseDao.forgetPassword(email)
    }


}