package com.haksoy.soip.ui.conversationList

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.haksoy.soip.R
import com.haksoy.soip.data.user.User
import com.haksoy.soip.databinding.ConversationDeteleMenuBinding
import com.haksoy.soip.databinding.FragmentConversationListBinding
import com.haksoy.soip.ui.main.SharedViewModel
import com.haksoy.soip.utlis.SwipeToDeleteCallback
import eightbitlab.com.blurview.RenderScriptBlur

class ConversationListFragment : Fragment(),
        ConversationListAdapter.ConversationListItemClickListener {

    private lateinit var binding: FragmentConversationListBinding
    private lateinit var adapter: ConversationListAdapter

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
            adapter = ConversationListAdapter(this, it)
            binding.chatRecyclerView.adapter = adapter
        })
        return binding.root
    }

    private fun setupViewPager() {
        binding.chatRecyclerView.setHasFixedSize(true)
        binding.chatRecyclerView.layoutManager =
                LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        binding.chatRecyclerView.addItemDecoration(
                DividerItemDecoration(
                        this.context,
                        DividerItemDecoration.VERTICAL
                )
        )
        val swipeHandler = object : SwipeToDeleteCallback() {
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                onRemoveRequest(viewHolder.adapterPosition)

            }
        }
        val itemTouchHelper = ItemTouchHelper(swipeHandler)
        itemTouchHelper.attachToRecyclerView(binding.chatRecyclerView)
    }

    private fun onRemoveRequest(position: Int) {
        val dialogBinding =
                ConversationDeteleMenuBinding.inflate(layoutInflater, null, false)

        dialogBinding.blurView.setupWith(binding.root)
                .setFrameClearDrawable(binding.root.background)
                .setBlurAlgorithm(RenderScriptBlur(context))
                .setBlurAutoUpdate(true)
                .setHasFixedTransformationMatrix(true)

        val builder = context?.let {
            AlertDialog.Builder(
                    it,
                    android.R.style.Theme_DeviceDefault_Light_NoActionBar
            ).setView(dialogBinding.root).setCancelable(false)
        }

        val dialog = builder!!.show()
        dialogBinding.btnCancel.setOnClickListener {
            adapter.notifyDataSetChanged()
            dialog.dismiss()
        }
        dialogBinding.btnDeleteForMe.text = getString(R.string.delete)
        dialogBinding.btnDeleteForMe.setOnClickListener {
            removeOnlyMe(position)
            dialog.dismiss()
        }
        dialogBinding.btnDeleteEveryone.visibility = View.GONE

//        val window = dialog.window!!
//        context?.let {
//            window.setBackgroundDrawable(ColorDrawable(it.getColor(R.color.delete_menu_background)))
//        }

    }


    private fun removeOnlyMe(position: Int) {
        viewModel.removeConversationAtPosition(position)
        adapter.removeAt(position)
    }

    override fun onClickedUser(user: User) {
        viewModel.markAsReadConversation(user)
        sharedViewModel.conversationDetailWithUser.postValue(user)
    }

}