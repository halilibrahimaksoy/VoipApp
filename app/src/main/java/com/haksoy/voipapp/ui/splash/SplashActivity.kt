package com.haksoy.voipapp.ui.splash

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.haksoy.voipapp.R
import com.haksoy.voipapp.ui.auth.AuthenticationActivity
import com.haksoy.voipapp.ui.main.MainActivity

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)


        val auth: FirebaseAuth = Firebase.auth
        val intent: Intent
        if (auth.currentUser != null) {
            intent = Intent(this, MainActivity::class.java)

//            findNavController().navigate(
//                R.id.action_loginFragment_to_userProfileFragment,
//                bundleOf(
//                    "isResistration" to true,
//                    "activeUID" to auth.currentUser!!.uid,
//                    "activeEmail" to auth.currentUser!!.email
//                )
//            )
        } else {
            intent = Intent(this, AuthenticationActivity::class.java)
        }
        startActivity(intent)
    }
}