package com.haksoy.soip.ui.main

import androidx.lifecycle.ViewModel
import com.haksoy.soip.data.FirebaseDao

class MainActivityViewModel : ViewModel() {
    private val firebaseDao = FirebaseDao.getInstance()
    val isUserDataExist = firebaseDao.isUserDataExist(firebaseDao.getCurrentUserUid())
}