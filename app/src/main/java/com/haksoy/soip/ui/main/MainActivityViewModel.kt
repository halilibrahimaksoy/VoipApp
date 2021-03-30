package com.haksoy.soip.ui.main

import androidx.lifecycle.ViewModel
import com.haksoy.soip.MainApplication
import com.haksoy.soip.data.FirebaseDao
import com.haksoy.soip.data.database.UserRepository
import com.haksoy.soip.utlis.observeOnce
import java.util.concurrent.Executors

class MainActivityViewModel : ViewModel() {
    private val firebaseDao = FirebaseDao.getInstance()
    val isUserDataExist = firebaseDao.isUserDataExist(firebaseDao.getCurrentUserUid())
    private val userRepository = UserRepository.getInstance(
        MainApplication.instance.applicationContext,
        Executors.newSingleThreadExecutor()
    )

    fun getUser(userUid:String)=
        userRepository.getUser(userUid)




}