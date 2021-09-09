package com.haksoy.soip.ui.auth.enterPhoneNumber

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import com.haksoy.soip.databinding.FragmentEnterPhoneNumberBinding
import com.haksoy.soip.ui.auth.AuthenticationViewModel
import com.haksoy.soip.utlis.getCountryDialCode


class EnterPhoneNumberFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    lateinit var binding: FragmentEnterPhoneNumberBinding

    private val viewModel by lazy {
        ViewModelProviders.of(this).get(AuthenticationViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
//            param1 = it.getString(ARG_PARAM1)
//            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentEnterPhoneNumberBinding.inflate(inflater, container, false)


        binding.countryCodeText.setText(context!!.getCountryDialCode())
        binding.countryCodeText.hint = context!!.getCountryDialCode()
        return binding.root
    }


    companion object {

        fun newInstance(param1: String, param2: String) =
            EnterPhoneNumberFragment().apply {
                arguments = Bundle().apply {
//                    putString(ARG_PARAM1, param1)
//                    putString(ARG_PARAM2, param2)
                }
            }
    }
}