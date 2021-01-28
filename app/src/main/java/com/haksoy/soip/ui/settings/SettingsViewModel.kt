package com.haksoy.soip.ui.settings

import androidx.lifecycle.ViewModel
import com.haksoy.soip.data.FirebaseDao

class SettingsViewModel : ViewModel() {
    private val firebaseDao = FirebaseDao.getInstance()

    fun signOut() {
        firebaseDao.auth.signOut()
    }
}