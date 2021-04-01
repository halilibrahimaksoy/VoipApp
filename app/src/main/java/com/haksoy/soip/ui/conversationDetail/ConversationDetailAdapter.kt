package com.haksoy.soip.ui.conversationDetail

import android.icu.text.SimpleDateFormat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.haksoy.soip.data.chat.Chat
import com.haksoy.soip.data.chat.ChatDirection
import com.haksoy.soip.databinding.ConversationDetailItemLeftBinding
import com.haksoy.soip.databinding.ConversationDetailItemRightBinding
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
            1 -> return RightViewHolder(
                ConversationDetailItemRightBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                ), listener
            )
            0 -> return LeftViewHolder(
                ConversationDetailItemLeftBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                ), listener
            )
        }
        return LeftViewHolder(
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
            (holder as LeftViewHolder).bind(items[position])
        else
            (holder as RightViewHolder).bind(items[position])

    }
}

class LeftViewHolder(
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

class RightViewHolder(
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