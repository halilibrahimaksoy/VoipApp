package com.haksoy.soip.ui.holdes

import android.icu.text.SimpleDateFormat
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.haksoy.soip.data.chat.Chat
import com.haksoy.soip.databinding.ConversationDetailItemLeftBinding
import com.haksoy.soip.ui.conversationDetail.ConversationDetailAdapter
import java.util.*

class ReceivedChatViewHolder(
        private val leftBinding: ConversationDetailItemLeftBinding,
        private val listener: ConversationDetailAdapter.ConversationDetailItemClickListener
) : RecyclerView.ViewHolder(leftBinding.root),
        View.OnClickListener {

    init {
        leftBinding.root.setOnClickListener(this)
    }

    private lateinit var chat: Chat
    fun bind(chat: Chat) {
        this.chat = chat
        leftBinding.txtMessage.text = chat.text.toString()
        val cal = Calendar.getInstance()
        cal.time = Date(chat.createDate)
        leftBinding.txtDate.text = SimpleDateFormat("HH:mm").format(chat.createDate)


    }

    override fun onClick(v: View?) {
        listener.onClickChat(chat)
    }
}