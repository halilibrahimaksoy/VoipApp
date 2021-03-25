package com.haksoy.soip.ui.conversationList

import android.icu.text.SimpleDateFormat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.haksoy.soip.data.chat.Conversation
import com.haksoy.soip.data.user.User
import com.haksoy.soip.databinding.ConversationListItemBinding
import java.util.*

class ConversationListAdapter(private val listener: ConversationListItemClickListener, val items: LinkedHashMap<User, Conversation>) :
        RecyclerView.Adapter<ConversationListViewHolder>() {

    interface ConversationListItemClickListener {
        fun onClickedUser(user: User)
    }

    fun removeAt(position: Int) {
        items.remove(items.keys.toList()[position])
        notifyItemRemoved(position)
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

    fun bind(user: User, chat: Conversation) {
        this.user = user
        itemBinding.txtFullName.text = user.name
        itemBinding.txtMessage.text = chat.text.toString()
        val cal = Calendar.getInstance()
        cal.time = Date(chat.createDate)
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