package com.haksoy.soip.ui.conversationDetail

import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.haksoy.soip.R
import com.haksoy.soip.data.chat.Chat
import com.haksoy.soip.data.chat.ChatDirection
import com.haksoy.soip.data.user.User
import com.haksoy.soip.databinding.ConversationDetailDeteleMenuBinding
import com.haksoy.soip.databinding.FragmentConversationDetailBinding
import com.haksoy.soip.ui.main.SharedViewModel
import com.haksoy.soip.utlis.Constants
import com.haksoy.soip.utlis.SwipeToDeleteCallback
import java.util.*


class ConversationDetailFragment : Fragment(), View.OnClickListener,
    ConversationDetailAdapter.ConversationDetailItemClickListener {
    companion object {
        fun newInstance(selectedUser: User? = null) = ConversationDetailFragment().apply {
            arguments = bundleOf(
                Constants.ConversationDetailFragmentSelectedUser to selectedUser
            )
        }
    }

    private val viewModel: ConversationDetailViewModel by viewModels()
    private val sharedViewModel: SharedViewModel by activityViewModels()

    private lateinit var binding: FragmentConversationDetailBinding
    private var adapter = ConversationDetailAdapter(this)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentConversationDetailBinding.inflate(inflater, container, false)

        if (activity is AppCompatActivity) {
            (activity as AppCompatActivity).setSupportActionBar(binding.toolbar)
        }
        (activity as AppCompatActivity).supportActionBar?.title = ""

        binding.btnSend.setOnClickListener(this)
        binding.btnBack.setOnClickListener(this)
        binding.imageView.setOnClickListener(this)
        binding.txtFullName.setOnClickListener(this)
        setupViewPager()
        fillUserData()
        return binding.root
    }

    private fun fillUserData() {
        if (viewModel.user.profileImage != null)
            showProfileImage(viewModel.user.profileImage!!)
        binding.txtFullName.text = viewModel.user.name

        viewModel.getConversationDetail(viewModel.user.uid)
        viewModel.conversationDetailList.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            adapter.setItems(it as ArrayList<Chat>)
            binding.recyclerView.scrollToPosition(0)
            viewModel.markAsRead()
        })
    }

    private fun showProfileImage(currentImageReferance: String) {
        Glide.with(binding.root /* context */)
            .load(currentImageReferance)
            .circleCrop()
            .into(binding.imageView)

    }

    private fun setupViewPager() {
        val linearLayoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        linearLayoutManager.reverseLayout = true
        linearLayoutManager.stackFromEnd = true
        binding.recyclerView.layoutManager = linearLayoutManager
        binding.recyclerView.adapter = adapter
        val swipeHandler = object : SwipeToDeleteCallback() {
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                onRemoveRequest(viewHolder.adapterPosition)
            }
        }
        val itemTouchHelper = ItemTouchHelper(swipeHandler)
        itemTouchHelper.attachToRecyclerView(binding.recyclerView)
    }

    private fun onRemoveRequest(position: Int) {
        val dialogBinding =
            ConversationDetailDeteleMenuBinding.inflate(layoutInflater, null, false)
        val builder = context?.let {
            AlertDialog.Builder(
                it,
                android.R.style.Theme_DeviceDefault_Light_NoActionBar_Fullscreen
            ).setView(dialogBinding.root).setCancelable(false)
        }

        val dialog = builder!!.show()
        dialogBinding.btnCancel.setOnClickListener {
            adapter.notifyDataSetChanged()
            dialog.dismiss()
        }
        dialogBinding.btnDeleteForMe.setOnClickListener {
            removeOnlyMe(position)
            dialog.dismiss()
        }
        if (viewModel.getChatDirection(position) == ChatDirection.InComing)
            dialogBinding.btnDeleteEveryone.visibility = View.GONE
        dialogBinding.btnDeleteEveryone.setOnClickListener {
            removeForEveryOne(position)
            dialog.dismiss()
        }
        val window = dialog.window!!
        context?.let {
            window.setBackgroundDrawable(ColorDrawable(it.getColor(R.color.delete_menu_background)))
        }

    }

    private fun removeForEveryOne(position: Int) {
        removeOnlyMe(position)
        viewModel.sendRemoveRequestAtPosition(position)
    }

    private fun removeOnlyMe(position: Int) {
        viewModel.removeChatAtPosition(position)
        adapter.removeAt(position)
    }

    private fun validateForm(): Boolean {
        var valid = true

        val email = binding.txtMessage.text.toString()
        if (TextUtils.isEmpty(email)) {
            binding.txtMessage.error = "Required."
            valid = false
        } else {
            binding.txtMessage.error = null
        }


        return valid
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.get(Constants.ConversationDetailFragmentSelectedUser)
            ?.let { viewModel.user = it as User }
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.btnSend -> {
                sendChat()
            }
            R.id.imageView,
            R.id.txtFullName -> {
                sharedViewModel.selectedUser.postValue(viewModel.user)
            }
            R.id.btnBack -> {
                activity?.onBackPressed()
            }
        }
    }

    private fun sendChat() {
        if (validateForm()) {
            viewModel.sendChat(
                binding.txtMessage.text.toString()
            )
            binding.txtMessage.setText("")
        }
    }

    override fun onClickChat(chat: Chat) {

    }

}