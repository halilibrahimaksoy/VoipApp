package com.haksoy.soip.ui.splash

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.haksoy.soip.R
import com.haksoy.soip.data.FirebaseDao
import com.haksoy.soip.ui.auth.AuthenticationActivity
import com.haksoy.soip.ui.main.MainActivity

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)


        val intent: Intent
        intent = if (FirebaseDao.getInstance().isAuthUserExist()) {
            Intent(this, MainActivity::class.java)
        } else {
            Intent(this, AuthenticationActivity::class.java)
        }
        startActivity(intent)
        finish()
    }
}