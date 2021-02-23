package com.haksoy.soip.ui.chat

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.haksoy.soip.chat.Chat
import com.haksoy.soip.data.entiries.User
import com.haksoy.soip.databinding.ChatListItemBinding

class ChatListAdapter(private val listener: ChatListItemClickListener) :
    RecyclerView.Adapter<ChatListViewHolder>() {

    interface ChatListItemClickListener {
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

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatListViewHolder {
        val binding: ChatListItemBinding =
            ChatListItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ChatListViewHolder(binding, listener)
    }

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: ChatListViewHolder, position: Int) {
        val user = items.keys.toMutableList()[position]
        holder.bind(user, items[user]!!)
    }
}

class ChatListViewHolder(
    private val itemBinding: ChatListItemBinding,
    private val listener: ChatListAdapter.ChatListItemClickListener
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
        itemBinding.txtDate.text = chat.createDate.toString()
        Glide.with(itemBinding.root /* context */)
            .load(user.profileImage)
            .circleCrop()
            .into(itemBinding.imageView)

    }

    override fun onClick(v: View?) {
        listener.onClickedUser(user)
    }
}