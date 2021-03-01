package com.haksoy.soip.ui.conversationDetail

import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.haksoy.soip.R
import com.haksoy.soip.data.chat.Chat
import com.haksoy.soip.data.chat.ChatDirection
import com.haksoy.soip.data.chat.ChatType
import com.haksoy.soip.data.user.User
import com.haksoy.soip.databinding.FragmentConversationDetailBinding
import com.haksoy.soip.ui.main.SharedViewModel
import com.haksoy.soip.utlis.Constants
import java.util.*

class ConversationDetailFragment : Fragment(), View.OnClickListener, ConversationDetailAdapter.ConversationDetailItemClickListener {
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
    private lateinit var user: User
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
        if (user.profileImage != null)
            showProfileImage(user.profileImage!!)
        binding.txtFullName.text = user.name

        viewModel.getConversationDetail(user.uid!!).observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            adapter.setItems(it as ArrayList<Chat>)
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
        arguments?.get(Constants.ConversationDetailFragmentSelectedUser)?.let { user = it as User }
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.btnSend -> {
                sendChat()
            }
            R.id.imageView,
            R.id.txtFullName -> {
                sharedViewModel.selectedUser.postValue(user)
            }
            R.id.btnBack -> {
                activity?.onBackPressed()
            }
        }
    }

    private fun sendChat() {
        if (validateForm()) {
            viewModel.sendChat(Chat(UUID.randomUUID(), user.uid!!, ChatDirection.OutGoing, true, ChatType.TEXT, binding.txtMessage.text.toString(), null, Date(), null))
            binding.txtMessage.setText("")
        }
    }

    override fun onClickChat(chat: Chat) {

    }

}