package com.haksoy.soip.ui.main

import android.app.Activity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.haksoy.soip.R
import com.haksoy.soip.data.user.User
import com.haksoy.soip.databinding.ActivityMainBinding
import com.haksoy.soip.databinding.CustomtabBinding
import com.haksoy.soip.ui.conversationDetail.ConversationDetailFragment
import com.haksoy.soip.ui.profile.UserProfileFragment
import com.haksoy.soip.ui.userlist.UserListFragment
import com.haksoy.soip.utlis.Constants
import com.haksoy.soip.utlis.Resource
import com.haksoy.soip.utlis.observeOnce


private const val TAG = "SoIP:MainActivity"

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val viewModel by lazy {
        ViewModelProviders.of(this).get(MainActivityViewModel::class.java)
    }
    private val sharedViewModel: SharedViewModel by viewModels()

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

        sharedViewModel.selectedUserUid.observe(this, Observer {
            Log.i(TAG, "sharedViewModel  :  selectedUserUid observed")

            showUserList()
        })
        sharedViewModel.selectedUser.observe(this, Observer {
            Log.i(TAG, "sharedViewModel  :  selectedUser observed")

            showUserProfileFragment(it)
        })
        intent.getStringExtra(Constants.ConversationDetailFragmentSelectedUser)?.let {
            viewModel.getUser(it).observeOnce {
                sharedViewModel.conversationDetailWithUser.postValue(it.data)
            }
        }

        sharedViewModel.conversationDetailWithUser.observe(this, Observer {
            showConversationDetailFragment(it)
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

    private fun showUserProfileFragment(user: User) {
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

    private fun showConversationDetailFragment(user: User) {
        val conversationDetailFragment =
                ConversationDetailFragment.newInstance(user)
        supportFragmentManager.beginTransaction()
                .replace(
                        R.id.usersFragment,
                        conversationDetailFragment,
                        Constants.UserProfileFragmentTag
                )
                .addToBackStack(Constants.ConversationDetailFragmentTag)
                .commit()
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
        val bindingTab1 = CustomtabBinding.inflate(layoutInflater)
        val bindingTab2 = CustomtabBinding.inflate(layoutInflater)
        val bindingTab3 = CustomtabBinding.inflate(layoutInflater)
        bindingTab1.icon.setBackgroundResource(R.mipmap.ic_map)
        bindingTab2.icon.setBackgroundResource(R.mipmap.ic_chat)
        bindingTab3.icon.setBackgroundResource(R.mipmap.ic_person)
        binding.tabs.getTabAt(0)!!.customView = bindingTab1.root
        binding.tabs.getTabAt(1)!!.customView = bindingTab2.root
        binding.tabs.getTabAt(2)!!.customView = bindingTab3.root

    }

    override fun onBackPressed() {
        hideKeyboard()
        for (fragment in supportFragmentManager.fragments) {
            if (fragment.isVisible && hasBackStack(fragment)) {
                if (popFragment(fragment)) return
            }
        }
        super.onBackPressed()
        if (supportFragmentManager.backStackEntryCount == 0)
            setClickable(false)
    }

    private fun hideKeyboard(){
        val imm: InputMethodManager =
            this.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        var view: View? = this.currentFocus
        if (view == null) {
            view = View(this)
        }
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }
    private fun hasBackStack(fragment: Fragment): Boolean {
        return fragment.childFragmentManager.backStackEntryCount > 0
    }

    private fun popFragment(fragment: Fragment): Boolean {
        val fragmentManager = fragment.childFragmentManager
        for (childFragment in fragment.childFragmentManager.fragments) {
            if (childFragment.isVisible) {
                return if (hasBackStack(childFragment)) {
                    popFragment(childFragment)
                } else {
                    fragmentManager.popBackStack()
                    true
                }
            }
        }
        return false
    }

}