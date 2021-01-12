package com.haksoy.voipapp.ui.auth

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.haksoy.voipapp.data.FirebaseDao
import com.haksoy.voipapp.utlis.Resource
import com.haksoy.voipapp.utlis.observeOnce

class AuthenticationViewModel : ViewModel() {
    val firebaseDao = FirebaseDao.getInstance()

    fun createAccount(email: String, password: String): LiveData<Resource<Exception>> {
        val resultModel = MutableLiveData<Resource<Exception>>()

        firebaseDao.createAccount(email, password).observeOnce {
            resultModel.postValue(it)
        }

        return resultModel
    }

    fun signIn(email: String, password: String): LiveData<Resource<Exception>> {
        val resultModel = MutableLiveData<Resource<Exception>>()

        firebaseDao.signIn(email, password).observeOnce {
            resultModel.postValue(it)
        }

        return resultModel
    }


}