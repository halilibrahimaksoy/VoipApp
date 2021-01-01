package com.haksoy.voipapp.ui.main

import android.content.Context
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.haksoy.voipapp.ui.chat.ChatFragment
import com.haksoy.voipapp.ui.discover.MapsFragment
import com.haksoy.voipapp.ui.profile.UserProfileFragment

class SectionsPagerAdapter(private val context: Context, fm: FragmentManager) :
    FragmentPagerAdapter(fm) {
    private var mapsFragment: MapsFragment = MapsFragment.newInstance()
    private var chatFragment: ChatFragment = ChatFragment.newInstance()
    private var userProfileFragment: UserProfileFragment = UserProfileFragment.newInstance(UserProfileFragment.Status.AUTH_USER)

    override fun getItem(position: Int): Fragment = when (position) {
        0 -> {
          mapsFragment
        }
        1 -> {
           chatFragment
        }
        else -> {
           userProfileFragment
        }
    }


    override fun getCount(): Int {
        return 3
    }
}