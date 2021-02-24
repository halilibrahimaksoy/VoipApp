package com.haksoy.soip.ui.conversationDetail

import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.haksoy.soip.R
import com.haksoy.soip.chat.Chat
import com.haksoy.soip.chat.ChatDirection
import com.haksoy.soip.chat.ChatType
import com.haksoy.soip.data.entiries.User
import com.haksoy.soip.databinding.FragmentConversationDetailBinding
import com.haksoy.soip.utlis.Constants
import java.util.*

class ConversationDetailFragment : Fragment(), View.OnClickListener {
    companion object {
        fun newInstance(selectedUser: User? = null) = ConversationDetailFragment().apply {
            arguments = bundleOf(
                    Constants.ConversationDetailFragmentSelectedUser to selectedUser
            )
        }
    }

    private val viewModel: ConversationDetailViewModel by viewModels()
    private lateinit var binding: FragmentConversationDetailBinding
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
        (activity as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(true)

        binding.btnSend.setOnClickListener(this)
        setupViewPager()
        fillUserData()
        return binding.root
    }

    private fun fillUserData() {
        if (user.profileImage != null)
            showProfileImage(user.profileImage!!)
        binding.txtFullName.text = user.name
    }

    private fun showProfileImage(currentImageReferance: String) {
        Glide.with(binding.root /* context */)
                .load(currentImageReferance)
                .circleCrop()
                .into(binding.imageView)

    }

    private fun setupViewPager() {
        binding.recyclerView.setHasFixedSize(true)
        binding.recyclerView.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
//        binding.recyclerView.adapter = adapter
    }

    private fun validateForm(): Boolean {
        var valid = true

        val email = binding.txtFullName.text.toString()
        if (TextUtils.isEmpty(email)) {
            binding.txtFullName.error = "Required."
            valid = false
        } else {
            binding.txtFullName.error = null
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
        }
    }

    private fun sendChat() {
        if (validateForm()) {
            viewModel.sendChat(Chat(UUID.randomUUID(), user.uid!!, ChatDirection.OutGoing, true, ChatType.TEXT, binding.txtMessage.text.toString(), null, Date(), null))
        }
    }

}