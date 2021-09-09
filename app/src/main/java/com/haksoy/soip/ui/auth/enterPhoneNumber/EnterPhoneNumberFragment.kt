package com.haksoy.soip.ui.auth.enterPhoneNumber

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import com.haksoy.soip.R
import com.haksoy.soip.databinding.FragmentEnterPhoneNumberBinding
import com.haksoy.soip.ui.auth.AuthenticationViewModel
import com.haksoy.soip.utlis.Constants


class EnterPhoneNumberFragment : Fragment() {
    lateinit var binding: FragmentEnterPhoneNumberBinding

    private val viewModel by lazy {
        ViewModelProviders.of(this).get(AuthenticationViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentEnterPhoneNumberBinding.inflate(inflater, container, false)


//        binding.countryCodeText.setText(context!!.getCountryDialCode())
//        binding.countryCodeText.hint = context!!.getCountryDialCode()

        binding.generateBtn.setOnClickListener {
            val country_code: String = binding.countryCodeText.text.toString()
            val phone_number: String = binding.phoneNumberText.text.toString()
            val complete_phone_number =
                "$country_code$phone_number"

            viewModel.verifyPhoneNumber(requireActivity(), complete_phone_number)
        }


        viewModel.verificationId.observe(viewLifecycleOwner, Observer {
            findNavController().navigate(R.id.action_enterPhoneNumberFragment_to_otpValidationFragment,Bundle().apply {
                putString(Constants.VerificationId, it)
            })
        })

        return binding.root
    }

}