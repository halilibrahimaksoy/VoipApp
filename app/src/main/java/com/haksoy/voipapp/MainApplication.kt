package com.haksoy.voipapp

import android.app.Application
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.ktx.Firebase

class MainApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        instance = this

    }

    companion object {
        lateinit var instance: MainApplication
            private set
    }
}