package com.haksoy.voipapp

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import com.haksoy.voipapp.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binging: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binging = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binging.root)

        val navHostFragment: NavHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment

    }


}
