package com.haksoy.soip.ui.main

import User
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.haksoy.soip.R
import com.haksoy.soip.databinding.ActivityMainBinding
import com.haksoy.soip.ui.profile.UserProfileFragment
import com.haksoy.soip.ui.userlist.UserListFragment
import com.haksoy.soip.ui.userlist.UserListViewModel
import com.haksoy.soip.utlis.Constants
import com.haksoy.soip.utlis.Resource
import com.haksoy.soip.utlis.observeOnce

private const val TAG = "MainActivity"

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
            Log.i(TAG, "userListViewModel  :  selectedUserUid observed")

            showUserList()
        })
        userListViewModel.selectedUser.observe(this, Observer {
            Log.i(TAG, "userListViewModel  :  selectedUser observed")

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
            .add(R.id.usersFragment, userProfileFragment, Constants.UserProfileFragmentTag)
            .commit()
    }

    private fun showUserDetailFragment(user: User) {
        val userProfileFragment =
            UserProfileFragment.newInstance(UserProfileFragment.Status.OTHER_USER, user)
        supportFragmentManager.beginTransaction()
            .replace(
                R.id.usersFragment,
                userProfileFragment,
                Constants.UserProfileFragmentTag
            )
            .addToBackStack(Constants.UserProfileFragmentTag)
            .commit()
        setClickable(true)
    }

    private fun showUserList() {
        supportFragmentManager.beginTransaction()
            .replace(
                R.id.usersFragment,
                UserListFragment.newInstance(),
                Constants.UserListFragmentTag
            ).addToBackStack(Constants.UserListFragmentTag)
            .commit()
        setClickable(true)
    }

    private fun setClickable(status: Boolean) {
        binding.usersFragment.isClickable = status
        binding.usersFragment.isFocusable = status
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

    override fun onBackPressed() {
        super.onBackPressed()
        if (supportFragmentManager.backStackEntryCount == 0)
            setClickable(false)
    }
}