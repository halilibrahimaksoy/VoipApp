package com.haksoy.soip.ui.gallery

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.CompositePageTransformer
import androidx.viewpager2.widget.MarginPageTransformer
import androidx.viewpager2.widget.ViewPager2
import com.haksoy.soip.data.chat.Chat
import com.haksoy.soip.databinding.FragmentMediaGalleryBinding
import com.haksoy.soip.utlis.Constants
import java.util.*
import kotlin.math.abs


/**
 * An example full-screen fragment that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
class MediaGalleryFragment : Fragment() {

    companion object {
        fun newInstance(userUid: String, chatUid: String) = MediaGalleryFragment().apply {
            arguments = bundleOf(
                Constants.ConversationMediaFragmentUser to userUid,
                Constants.ConversationMediaFragmentSelectedChat to chatUid
            )
        }
    }

    private val viewModel: MediaGalleryViewModel by viewModels()
    private lateinit var binding: FragmentMediaGalleryBinding
    private var adapter = MediaGalleryAdapter()
    private lateinit var userUid: String
    private lateinit var chatUid: String
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMediaGalleryBinding.inflate(inflater, container, false)
        setupViewPager()
        fillData()
        return binding.root
    }

    private fun fillData() {
        viewModel.getConversationMedia(userUid).observe(viewLifecycleOwner, Observer {
            adapter.setItems(it as ArrayList<Chat>)
//            binding.recyclerView.scrollToPosition(0)
        })
    }

    private fun setupViewPager() {
        binding.viewPager.adapter = adapter
        binding.viewPager.clipToPadding = false
        binding.viewPager.clipChildren = false
        binding.viewPager.offscreenPageLimit = 3
        binding.viewPager.getChildAt(0).overScrollMode = RecyclerView.OVER_SCROLL_NEVER
        val compositeTransformer = CompositePageTransformer()
        compositeTransformer.addTransformer(MarginPageTransformer(20))
        compositeTransformer.addTransformer(ViewPager2.PageTransformer { page, position ->
            val r = 1 - abs(position)
            page.scaleY = 0.75f + r * 0.25f
            page.scaleX = 0.75f + r * 0.25f
        })
        binding.viewPager.setPageTransformer(compositeTransformer)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.get(Constants.ConversationMediaFragmentUser)?.let {
            userUid = it as String
        }
        arguments?.get(Constants.ConversationMediaFragmentSelectedChat)?.let {
            chatUid = it as String
        }
    }
//    override fun onResume() {
//        super.onResume()
//        activity?.window?.addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)
//    }
//
//
//    override fun onPause() {
//        super.onPause()
//        activity?.window?.clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)
//
//        // Clear the systemUiVisibility flag
//        activity?.window?.decorView?.systemUiVisibility = 0
//    }


}