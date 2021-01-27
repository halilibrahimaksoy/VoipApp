package com.haksoy.voipapp.ui.settings

import androidx.lifecycle.ViewModel
import com.haksoy.voipapp.data.FirebaseDao

class SettingsViewModel : ViewModel() {
    private val firebaseDao = FirebaseDao.getInstance()

    fun signOut() {
        firebaseDao.auth.signOut()
    }
}