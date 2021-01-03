package com.haksoy.voipapp.ui.chat

import android.Manifest
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import com.haksoy.voipapp.R
import com.haksoy.voipapp.utlis.hasPermission

class ChatFragment : Fragment() {

    companion object {
        fun newInstance() = ChatFragment()
    }

    private val viewModel by lazy {
        ViewModelProviders.of(this).get(ChatViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        if (!activity?.hasPermission(Manifest.permission.ACCESS_FINE_LOCATION)!!)
            requestPermissions(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),0)
        return inflater.inflate(R.layout.chat_fragment, container, false)
    }

    override fun onStart() {
        super.onStart()
        viewModel.startLocationUpdates()
    }

    override fun onStop() {
        super.onStop()
        viewModel.stopLocationUpdates()
    }

}