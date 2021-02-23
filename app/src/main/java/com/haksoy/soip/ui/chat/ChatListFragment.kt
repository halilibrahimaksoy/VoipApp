package com.haksoy.soip.ui.chat

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import com.haksoy.soip.databinding.FragmentChatListBinding

class ChatListFragment : Fragment(), ChatListAdapter.ChatListItemClickListener {

    private lateinit var binding: FragmentChatListBinding
    private var adapter = ChatListAdapter(this)

    companion object {
        fun newInstance() = ChatListFragment()
    }

    private val viewModel: ChatListViewModel by activityViewModels()

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        binding = FragmentChatListBinding.inflate(inflater, container, false)
        setupViewPager()
        viewModel.chatListWithUserLiveData.observe(viewLifecycleOwner, Observer {
            adapter.setItems(it)
        })
        return binding.root
    }

    private fun setupViewPager() {
        binding.chatRecyclerView.adapter = adapter

    }

    override fun onClickedUser(user: com.haksoy.soip.data.entiries.User) {
        println("asdf")
    }

}