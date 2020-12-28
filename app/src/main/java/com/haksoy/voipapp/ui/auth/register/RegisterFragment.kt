package com.haksoy.voipapp.ui.auth.register

import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.haksoy.voipapp.databinding.RegisterFragmentBinding
import com.haksoy.voipapp.ui.auth.AuthenticationViewModel
import com.haksoy.voipapp.utlis.Resource
import kotlinx.android.synthetic.main.register_fragment.*

class RegisterFragment : Fragment() {
    private lateinit var binding: RegisterFragmentBinding
    private lateinit var viewModel: AuthenticationViewModel
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = RegisterFragmentBinding.inflate(inflater, container, false)

        binding.btnRegister.setOnClickListener(View.OnClickListener {
            if (validateForm()) {
                createAccount()
            }
        })
        return binding.root
    }

    private fun createAccount() {
        viewModel.createAccount(
            binding.txtEmail.text.toString(),
            binding.txtPassword.text.toString()
        ).observe(viewLifecycleOwner,
            Observer {
                if (it.status == Resource.Status.SUCCESS) {
                    singIn()
                } else if (it.status == Resource.Status.ERROR) {
                    Toast.makeText(activity, it.message, Toast.LENGTH_SHORT)
                        .show()
                }

            })
    }

    private fun singIn() {
        viewModel.signIn(
            binding.txtEmail.text.toString(),
            binding.txtPassword.text.toString()
        ).observe(viewLifecycleOwner,
            Observer {
                if (it.status == Resource.Status.ERROR) {
                    Toast.makeText(activity, it.message, Toast.LENGTH_SHORT)
                        .show()
                }
            })
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = AuthenticationViewModel()
    }

    private fun validateForm(): Boolean {
        var valid = true

        val email = binding.txtEmail.text.toString()
        if (TextUtils.isEmpty(email)) {
            binding.txtEmail.error = "Required."
            valid = false
        } else {
            binding.txtEmail.error = null
        }

        val password = binding.txtPassword.text.toString()
        if (TextUtils.isEmpty(password)) {
            binding.txtPassword.error = "Required."
            valid = false
        } else {
            binding.txtPassword.error = null
        }
        val fullName = binding.txtPassword2.text.toString()
        if (TextUtils.isEmpty(fullName)) {
            binding.txtPassword2.error = "Required."
            valid = false
        } else {
            binding.txtPassword2.error = null
        }

        if (!txtPassword.equals(txtPassword2)) {
            binding.txtPassword.error = "Should be same."
            binding.txtPassword2.error = "Should be same."
            Toast.makeText(activity, "Passwords doesn't match", Toast.LENGTH_SHORT).show()
        }


        return valid
    }
}