package com.haksoy.voipapp.ui.auth

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.haksoy.voipapp.databinding.ActivityAuthenticationBinding

class AuthenticationActivity : AppCompatActivity() {

    lateinit var binding: ActivityAuthenticationBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityAuthenticationBinding.inflate(layoutInflater)
        setContentView(binding.root)

    }


}