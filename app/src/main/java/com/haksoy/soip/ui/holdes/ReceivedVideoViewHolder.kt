package com.haksoy.soip.ui.holdes

import android.icu.text.SimpleDateFormat
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.haksoy.soip.data.chat.Chat
import com.haksoy.soip.databinding.ReceivedVideoItemBinding
import com.haksoy.soip.ui.conversationDetail.ConversationDetailAdapter
import java.util.*

class ReceivedVideoViewHolder(private val binding: ReceivedVideoItemBinding,
                              private val listener: ConversationDetailAdapter.ConversationDetailItemClickListener
) : RecyclerView.ViewHolder(binding.root),
        View.OnClickListener {

    init {
        binding.root.setOnClickListener(this)
    }

    private lateinit var chat: Chat
    fun bind(chat: Chat) {
        this.chat = chat
        binding.videoView.setVideoPath(chat.contentUrl)
        binding.videoView.setOnClickListener {
            if (binding.videoView.isPlaying)
                binding.videoView.pause()
            else
                binding.videoView.start()
        }
        val cal = Calendar.getInstance()
        cal.time = Date(chat.createDate)
        binding.txtDate.text = SimpleDateFormat("HH:mm").format(chat.createDate)

    }

    override fun onClick(v: View?) {
        listener.onClickChat(chat)
    }
}