package com.haksoy.soip.ui.holdes

import android.icu.text.SimpleDateFormat
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.haksoy.soip.data.chat.Chat
import com.haksoy.soip.databinding.SendImageItemBinding
import com.haksoy.soip.ui.conversationDetail.ConversationDetailAdapter
import java.util.*

class SendImageViewHolder(private val binding: SendImageItemBinding,
                          private val listener: ConversationDetailAdapter.ConversationDetailItemClickListener
) : RecyclerView.ViewHolder(binding.root),
        View.OnClickListener {

    init {
        binding.root.setOnClickListener(this)
    }

    private lateinit var chat: Chat
    fun bind(chat: Chat) {
        this.chat = chat
        Glide.with(binding.root).load(chat.contentUrl).into(binding.imgMsg)
        val cal = Calendar.getInstance()
        cal.time = Date(chat.createDate)
        binding.txtDate.text = SimpleDateFormat("HH:mm").format(chat.createDate)

    }

    override fun onClick(v: View?) {
        listener.onClickChat(chat)
    }
}