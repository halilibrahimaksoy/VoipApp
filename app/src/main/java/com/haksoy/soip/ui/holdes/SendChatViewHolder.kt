package com.haksoy.soip.ui.holdes

import android.icu.text.SimpleDateFormat
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.haksoy.soip.data.chat.Chat
import com.haksoy.soip.databinding.ConversationDetailItemRightBinding
import com.haksoy.soip.ui.conversationDetail.ConversationDetailAdapter
import java.util.*

class SendChatViewHolder(
        private val binding: ConversationDetailItemRightBinding,
        private val listener: ConversationDetailAdapter.ConversationDetailItemClickListener
) : RecyclerView.ViewHolder(binding.root),
        View.OnClickListener {

    init {
        binding.root.setOnClickListener(this)
    }

    private lateinit var chat: Chat
    fun bind(chat: Chat) {
        this.chat = chat
        binding.txtMessage.text = chat.text.toString()
        val cal = Calendar.getInstance()
        cal.time = Date(chat.createDate)
        binding.txtDate.text = SimpleDateFormat("HH:mm").format(chat.createDate)

    }

    override fun onClick(v: View?) {
        listener.onClickChat(chat)
    }
}