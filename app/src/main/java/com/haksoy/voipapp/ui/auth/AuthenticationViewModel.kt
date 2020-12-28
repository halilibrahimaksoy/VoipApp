package com.haksoy.voipapp.ui.auth

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.haksoy.voipapp.utlis.Resource

class AuthenticationViewModel : ViewModel() {
    val auth = MutableLiveData<Resource<FirebaseAuth>>()
    val userDao = AuthenticationDao()
    init {

    }
    fun createAccount(email: String, password: String): LiveData<Resource<FirebaseAuth>> {
        val auth = MutableLiveData<Resource<FirebaseAuth>>()
        val userDao = AuthenticationDao()
        userDao.createAccount(email, password).observeForever {
            auth.postValue(it)
        }

        return auth
    }

    fun signIn(email: String, password: String): LiveData<Resource<FirebaseAuth>> {

        userDao.signIn(email, password).observeForever {
            auth.postValue(it)
        }

        return auth
    }
}