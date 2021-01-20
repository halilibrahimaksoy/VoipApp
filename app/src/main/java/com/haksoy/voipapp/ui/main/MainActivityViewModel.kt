package com.haksoy.voipapp.ui.main

import androidx.lifecycle.ViewModel
import com.haksoy.voipapp.data.FirebaseDao

class MainActivityViewModel : ViewModel() {
    private val firebaseDao = FirebaseDao.getInstance()
    val isUserDataExist = firebaseDao.isUserDataExist(firebaseDao.getCurrentUserUid())
}