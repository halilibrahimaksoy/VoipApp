package com.haksoy.voipapp.ui.userlist

import User
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.CompositePageTransformer
import androidx.viewpager2.widget.MarginPageTransformer
import androidx.viewpager2.widget.ViewPager2
import com.haksoy.voipapp.databinding.FragmentUserListBinding
import com.haksoy.voipapp.utlis.Constants
import org.parceler.Parcels


class UserListFragment : Fragment(), UserListAdapter.UserItemListener {

    companion object {
        @JvmStatic
        fun newInstance(userList: List<User>,selectedUserUid:String): UserListFragment {
            return UserListFragment().apply {
                arguments = Bundle().apply {
                    putParcelable(Constants.NearlyUserList, Parcels.wrap(userList))
                    putString(Constants.SelectedUserUid,selectedUserUid)
                }
            }
        }
    }

    private lateinit var binding: FragmentUserListBinding
    private lateinit var adapter: UserListAdapter
    private lateinit var userList: List<User>
    private lateinit var selectedUserUid: String
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Log.d("UserListFragment", "onCreateView")
        binding = FragmentUserListBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViewPager()
    }

    private fun setupViewPager() {
        adapter = UserListAdapter(this)
        adapter.setItems(userList as ArrayList<User>)
        binding.userViewPager.adapter = adapter
        binding.userViewPager.clipToPadding = false
        binding.userViewPager.clipChildren = false
        binding.userViewPager.offscreenPageLimit = 3
        binding.userViewPager.getChildAt(0).overScrollMode = RecyclerView.OVER_SCROLL_NEVER
        val compositeTransformer = CompositePageTransformer()
        compositeTransformer.addTransformer(MarginPageTransformer(40))
        compositeTransformer.addTransformer(ViewPager2.PageTransformer { page, position ->
            val r = 1 - Math.abs(position)
            page.scaleY = 0.75f + r * 0.25f
            page.scaleX = 0.75f + r * 0.25f
        })
        binding.userViewPager.setPageTransformer(compositeTransformer)


        binding.userViewPager.setCurrentItem(getPositionFromUid(selectedUserUid))
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        userList = Parcels.unwrap<List<User>>(arguments?.getParcelable(Constants.NearlyUserList))
        selectedUserUid = arguments?.getString(Constants.SelectedUserUid).toString()
    }

    fun getPositionFromUid(uid: String): Int {
        for (i in 0..userList.size) {
            if (userList[i].uid == uid)
                return Int.MAX_VALUE/2+i
        }
        return -1
    }


    override fun onClickedUser(user: User) {

    }
}