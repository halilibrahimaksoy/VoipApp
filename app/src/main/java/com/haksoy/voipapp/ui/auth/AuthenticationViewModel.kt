package com.haksoy.voipapp.ui.auth

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.haksoy.voipapp.data.FirebaseDao
import com.haksoy.voipapp.utlis.Resource
import com.haksoy.voipapp.utlis.observeOnce

class AuthenticationViewModel : ViewModel() {
    val resultModel = MutableLiveData<Resource<Exception>>()
    val userDao = FirebaseDao.getInstance()

    fun createAccount(email: String, password: String): LiveData<Resource<Exception>> {
        userDao.createAccount(email, password).observeOnce {
            resultModel.postValue(it)
        }

        return resultModel
    }

    fun signIn(email: String, password: String): LiveData<Resource<Exception>> {

        userDao.signIn(email, password).observeOnce {
            resultModel.postValue(it)
        }

        return resultModel
    }

}