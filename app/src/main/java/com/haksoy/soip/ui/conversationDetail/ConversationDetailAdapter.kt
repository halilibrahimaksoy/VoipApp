package com.haksoy.soip.ui.conversationDetail

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.haksoy.soip.data.chat.Chat
import com.haksoy.soip.data.chat.ChatDirection
import com.haksoy.soip.databinding.ConversationDetailItemLeftBinding
import com.haksoy.soip.databinding.ConversationDetailItemRightBinding
import com.haksoy.soip.ui.holdes.ReceivedChatViewHolder
import com.haksoy.soip.ui.holdes.SendChatViewHolder
import java.util.*

class ConversationDetailAdapter(private val listener: ConversationDetailItemClickListener) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private val items = ArrayList<Chat>()

    fun setItems(items: ArrayList<Chat>) {
        this.items.clear()
        this.items.addAll(items)
        notifyDataSetChanged()
    }

    interface ConversationDetailItemClickListener {
        fun onClickChat(chat: Chat)
    }

    fun removeAt(position: Int) {
        items.remove(items[position])
        notifyItemRemoved(position)
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        when (viewType) {
            1 -> return SendChatViewHolder(
                ConversationDetailItemRightBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                ), listener
            )
            0 -> return ReceivedChatViewHolder(
                ConversationDetailItemLeftBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                ), listener
            )
        }
        return ReceivedChatViewHolder(
            ConversationDetailItemLeftBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            ), listener
        )
    }

    override fun getItemCount(): Int = items.size

    override fun getItemViewType(position: Int): Int {
        return items[position].direction.ordinal
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (items[position].direction == ChatDirection.InComing)
            (holder as ReceivedChatViewHolder).bind(items[position])
        else
            (holder as SendChatViewHolder).bind(items[position])

    }
}



