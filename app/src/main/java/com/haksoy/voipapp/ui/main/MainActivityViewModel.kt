package com.haksoy.voipapp.ui.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.haksoy.voipapp.data.FirebaseDao
import com.haksoy.voipapp.utlis.Resource

class MainActivityViewModel : ViewModel() {
    private val firebaseDao = FirebaseDao.getInstance()
    fun isUserDataExist(): LiveData<Resource<Boolean>> {
        return firebaseDao.isUserDataExist(firebaseDao.getCurrentUserUid())
    }
}