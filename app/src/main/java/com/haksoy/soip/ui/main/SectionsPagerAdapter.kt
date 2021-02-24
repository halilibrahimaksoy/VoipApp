package com.haksoy.soip.ui.main

import android.content.Context
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.haksoy.soip.ui.conversationList.ConversationListFragment
import com.haksoy.soip.ui.discover.MapsFragment
import com.haksoy.soip.ui.profile.UserProfileFragment

class SectionsPagerAdapter(private val context: Context, fm: FragmentManager) :
    FragmentPagerAdapter(fm) {
    private var mapsFragment: MapsFragment = MapsFragment.newInstance()
    private var conversationFragment: ConversationListFragment = ConversationListFragment.newInstance()
    private var userProfileFragment: UserProfileFragment = UserProfileFragment.newInstance(UserProfileFragment.Status.AUTH_USER)

    override fun getItem(position: Int): Fragment = when (position) {
        0 -> {
          mapsFragment
        }
        1 -> {
           conversationFragment
        }
        else -> {
           userProfileFragment
        }
    }


    override fun getCount(): Int {
        return 3
    }
}