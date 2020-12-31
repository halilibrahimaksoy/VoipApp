package com.haksoy.voipapp.ui.main

import android.content.Context
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.haksoy.voipapp.R
import com.haksoy.voipapp.ui.chat.ChatFragment
import com.haksoy.voipapp.ui.discover.MapsFragment
import com.haksoy.voipapp.ui.profile.UserProfileFragment

/**
 * A [FragmentPagerAdapter] that returns a fragment corresponding to
 * one of the sections/tabs/pages.
 */
class SectionsPagerAdapter(private val context: Context, fm: FragmentManager) :
    FragmentPagerAdapter(fm) {

    override fun getItem(position: Int): Fragment = when (position) {
        0 -> {
            MapsFragment.newInstance()
        }
        1 -> {
            ChatFragment.newInstance()
        }
        else ->{
            UserProfileFragment.newInstance()
        }
    }


    override fun getCount(): Int {
        // Show 2 total pages.
        return 3
    }
}