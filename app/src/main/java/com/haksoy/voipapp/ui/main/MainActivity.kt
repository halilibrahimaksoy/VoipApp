package com.haksoy.voipapp.ui.main

import User
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.haksoy.voipapp.R
import com.haksoy.voipapp.databinding.ActivityMainBinding
import com.haksoy.voipapp.ui.profile.UserProfileFragment
import com.haksoy.voipapp.ui.userlist.UserListFragment
import com.haksoy.voipapp.ui.userlist.UserListViewModel
import com.haksoy.voipapp.utlis.Constants
import com.haksoy.voipapp.utlis.Resource
import com.haksoy.voipapp.utlis.observeOnce

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val viewModel by lazy {
        ViewModelProviders.of(this).get(MainActivityViewModel::class.java)
    }
    private val userListViewModel: UserListViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)

        setContentView(binding.root)


        viewModel.isUserDataExist.observeOnce {
            if (it.status == Resource.Status.SUCCESS) {
                if (!it.data!!) {
                    showUserUpdateFragment()
                } else
                    prepareUi()
            }
        }

        userListViewModel.selectedUserUid.observe(this, Observer {
            showUserList()
        })
        userListViewModel.selectedUser.observe(this, Observer {
            showUserDetailFragment(it)
        })
    }

    private fun prepareUi() {
        setViewPager()
        setupTabIcons()
    }

    private fun showUserUpdateFragment() {
        val userProfileFragment =
            UserProfileFragment.newInstance(UserProfileFragment.Status.REGISTRATION)
        supportFragmentManager.beginTransaction()
            .add(R.id.userProfileFragment, userProfileFragment, Constants.UserProfileFragmentTag)
            .commit()
    }

    private fun showUserDetailFragment(user: User) {
        val userProfileFragment =
            UserProfileFragment.newInstance(UserProfileFragment.Status.OTHER_USER, user)
        supportFragmentManager.beginTransaction()
            .replace(R.id.userProfileFragment, userProfileFragment, Constants.UserProfileFragmentTag)
            .addToBackStack(Constants.UserProfileFragmentTag)
            .commit()
    }

    private fun showUserList() {
        val fragment = supportFragmentManager.findFragmentByTag(Constants.UserListFragmentTag)
        if (fragment == null) {
            supportFragmentManager.beginTransaction()
                .replace(
                    R.id.userProfileFragment,
                    UserListFragment.newInstance(),
                    Constants.UserListFragmentTag
                ).addToBackStack(Constants.UserListFragmentTag)
                .commit()
        }
    }

    private fun setViewPager() {
        val sectionsPagerAdapter =
            SectionsPagerAdapter(
                this,
                supportFragmentManager
            )
        binding.viewPager.adapter = sectionsPagerAdapter
        binding.viewPager.offscreenPageLimit = 3
        binding.tabs.setupWithViewPager(binding.viewPager)
    }

    private fun setupTabIcons() {
        binding.tabs.getTabAt(0)!!.icon = getDrawable(R.mipmap.ic_map)
        binding.tabs.getTabAt(1)!!.icon = getDrawable(R.mipmap.ic_chat)
        binding.tabs.getTabAt(2)!!.icon = getDrawable(R.mipmap.ic_person)

    }

}