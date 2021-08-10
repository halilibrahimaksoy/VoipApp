package com.haksoy.soip.ui.holdes

import androidx.recyclerview.widget.RecyclerView
import com.haksoy.soip.data.chat.Chat
import com.haksoy.soip.databinding.VideoGalleryItemBinding

class VideoGalleryViewHolder(private val binding: VideoGalleryItemBinding) :
    RecyclerView.ViewHolder(binding.root) {

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

    }
}