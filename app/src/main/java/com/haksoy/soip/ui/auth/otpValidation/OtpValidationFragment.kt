package com.haksoy.soip.ui.auth.otpValidation

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.haksoy.soip.databinding.FragmentOtpValidationBinding
import com.haksoy.soip.ui.auth.AuthenticationViewModel
import com.haksoy.soip.ui.main.MainActivity
import com.haksoy.soip.utlis.Constants
import com.haksoy.soip.utlis.Resource
import com.haksoy.soip.utlis.observeWithProgress

class OtpValidationFragment : Fragment() {
    private lateinit var binding: FragmentOtpValidationBinding
    private val viewModel by lazy {
        ViewModelProviders.of(this).get(AuthenticationViewModel::class.java)
    }

    lateinit var verificationId: String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            verificationId = it.getString(Constants.VerificationId)!!
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentOtpValidationBinding.inflate(inflater, container, false)
        // Inflate the layout for this fragment
        binding.verifyBtn.setOnClickListener {
            viewModel.signInWithPhoneAuthCredential(
                verificationId,
                binding.otpTextView.text.toString()
            )
                .observeWithProgress(
                    requireContext(), viewLifecycleOwner,
                    Observer {
                        if (it.status == Resource.Status.SUCCESS) {
                            startActivity(Intent(activity, MainActivity::class.java))
                            activity?.finish()
                        } else if (it.status == Resource.Status.ERROR) {
                            it.data?.let {
//                                    it1 -> handleError(it1)
                            }
                        }
                    })
        }
        return binding.root
    }

    companion object {
        fun newInstance(param1: String) =
            OtpValidationFragment().apply {
                arguments = Bundle().apply {
                    putString(Constants.VerificationId, param1)
//                    putString(ARG_PARAM2, param2)
                }
            }
    }
}