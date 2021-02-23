package com.haksoy.soip.ui.userlist

import com.haksoy.soip.data.entiries.User
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.haksoy.soip.databinding.UserItemBinding

class UserListAdapter(private val listener: UserItemListener) :
    RecyclerView.Adapter<UserViewHolder>() {

    interface UserItemListener {
        fun onClickedUser(user: User)
    }

    private val items = ArrayList<User>()

    fun setItems(items: ArrayList<User>) {
        this.items.clear()
        this.items.addAll(items)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val binding: UserItemBinding =
            UserItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return UserViewHolder(binding, listener)
    }

    override fun getItemCount(): Int = Int.MAX_VALUE

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        if (items.size > 0) {
            holder.bind(items[position % items.size])
        }
    }
}

class UserViewHolder(
    private val itemBinding: UserItemBinding,
    private val listener: UserListAdapter.UserItemListener
) : RecyclerView.ViewHolder(itemBinding.root),
    View.OnClickListener {

    private lateinit var user: User

    init {
        itemBinding.root.setOnClickListener(this)
    }

    fun bind(item: User) {
        this.user = item
        itemBinding.txtFullName.text = item.name
        itemBinding.txtInfo.text = item.info
        Glide.with(itemBinding.root /* context */)
            .load(item.profileImage)
            .circleCrop()
            .into(itemBinding.imageView)

    }

    override fun onClick(v: View?) {
        listener.onClickedUser(user)
    }
}