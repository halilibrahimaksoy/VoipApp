package com.haksoy.soip.ui.conversationList

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.haksoy.soip.data.user.User
import com.haksoy.soip.databinding.FragmentConversationListBinding
import com.haksoy.soip.ui.main.SharedViewModel
import com.haksoy.soip.utlis.SwipeToDeleteCallback

class ConversationListFragment : Fragment(),
    ConversationListAdapter.ConversationListItemClickListener {

    private lateinit var binding: FragmentConversationListBinding
    private var adapter = ConversationListAdapter(this)

    companion object {
        fun newInstance() = ConversationListFragment()
    }

    private val viewModel: ConversationListViewModel by viewModels()
    private val sharedViewModel: SharedViewModel by activityViewModels()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentConversationListBinding.inflate(inflater, container, false)
        setupViewPager()
        viewModel.conversationWithUserLiveData.observe(viewLifecycleOwner, Observer {
            adapter.setItems(it)
        })
        return binding.root
    }

    private fun setupViewPager() {
        binding.chatRecyclerView.setHasFixedSize(true)
        binding.chatRecyclerView.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        binding.chatRecyclerView.adapter = adapter
        context?.let {
            val swipeHandler = object : SwipeToDeleteCallback(it) {
                override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                    adapter.removeAt(viewHolder.adapterPosition)
                }
            }
            val itemTouchHelper = ItemTouchHelper(swipeHandler)
            itemTouchHelper.attachToRecyclerView(binding.chatRecyclerView)
        }

    }

    override fun onClickedUser(user: User) {
        sharedViewModel.conversationDetailWithUser.postValue(user)
    }

}