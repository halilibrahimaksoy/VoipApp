package com.haksoy.soip.ui.conversationList

import android.icu.text.SimpleDateFormat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.haksoy.soip.chat.Chat
import com.haksoy.soip.data.entiries.User
import com.haksoy.soip.databinding.ConversationListItemBinding
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.collections.LinkedHashMap

class ConversationListAdapter(private val listener: ConversationListItemClickListener) :
    RecyclerView.Adapter<ConversationListViewHolder>() {

    interface ConversationListItemClickListener {
        fun onClickedUser(user: User)
    }

    private val items = LinkedHashMap<User, Chat>()

    fun setItems(items: LinkedHashMap<User, Chat>) {
        this.items.clear()
        this.items.putAll(items)
        notifyDataSetChanged()
    }

    fun addItems(items: LinkedHashMap<User, Chat>) {
        this.items.putAll(items)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ConversationListViewHolder {
        val binding: ConversationListItemBinding =
            ConversationListItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ConversationListViewHolder(binding, listener)
    }

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: ConversationListViewHolder, position: Int) {
        val user = items.keys.toMutableList()[position]
        holder.bind(user, items[user]!!)
    }
}

class ConversationListViewHolder(
    private val itemBinding: ConversationListItemBinding,
    private val listener: ConversationListAdapter.ConversationListItemClickListener
) : RecyclerView.ViewHolder(itemBinding.root),
    View.OnClickListener {

    private lateinit var user: User

    init {
        itemBinding.root.setOnClickListener(this)
    }

    fun bind(user: User, chat: Chat) {
        this.user = user
        itemBinding.txtFullName.text = user.name
        itemBinding.txtMessage.text = chat.text.toString()
        val cal = Calendar.getInstance()
        cal.time = chat.createDate
        itemBinding.txtDate.text = SimpleDateFormat("HH:mm").format(chat.createDate)
        Glide.with(itemBinding.root /* context */)
            .load(user.profileImage)
            .circleCrop()
            .into(itemBinding.imageView)

    }

    override fun onClick(v: View?) {
        listener.onClickedUser(user)
    }
}