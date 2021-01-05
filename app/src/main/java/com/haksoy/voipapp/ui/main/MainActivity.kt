package com.haksoy.voipapp.ui.main

import android.Manifest
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.get
import com.haksoy.voipapp.R
import com.haksoy.voipapp.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)

        setContentView(binding.root)
        setViewPager()
        setupTabIcons()

    }

    private fun setViewPager() {
        val sectionsPagerAdapter =
            SectionsPagerAdapter(
                this,
                supportFragmentManager
            )
        binding.viewPager.adapter = sectionsPagerAdapter
        binding.viewPager.offscreenPageLimit=3
        binding.tabs.setupWithViewPager(binding.viewPager)
    }

    private fun setupTabIcons() {
        binding.tabs.getTabAt(0)!!.icon = getDrawable(R.mipmap.ic_map)
        binding.tabs.getTabAt(1)!!.icon = getDrawable(R.mipmap.ic_chat)
        binding.tabs.getTabAt(2)!!.icon = getDrawable(R.mipmap.ic_person)

    }
}