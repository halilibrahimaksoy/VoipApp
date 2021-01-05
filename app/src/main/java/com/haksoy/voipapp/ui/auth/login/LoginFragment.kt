package com.haksoy.voipapp.ui.auth.login

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.haksoy.voipapp.R
import com.haksoy.voipapp.databinding.LoginFragmentBinding
import com.haksoy.voipapp.ui.auth.AuthenticationViewModel
import com.haksoy.voipapp.ui.main.MainActivity
import com.haksoy.voipapp.utlis.Resource

class LoginFragment : Fragment() {

    private lateinit var binding: LoginFragmentBinding
    private val viewModel by lazy {
        ViewModelProviders.of(this).get(AuthenticationViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = LoginFragmentBinding.inflate(inflater, container, false)

        binding.btnRegister.setOnClickListener(View.OnClickListener {
            findNavController().navigate(R.id.action_loginFragment_to_registerFragment)
        })
        binding.btnLogin.setOnClickListener(View.OnClickListener {
            if (validateForm()) {
                viewModel.signIn(
                    binding.txtEmail.text.toString(),
                    binding.txtPassword.text.toString()
                )
                    .observe(viewLifecycleOwner,
                        Observer {
                            if (it.status == Resource.Status.SUCCESS) {
                                startActivity(Intent(activity, MainActivity::class.java))
                            } else if (it.status == Resource.Status.ERROR) {
                                it.data?.let { it1 -> handleError(it1) }
                            }
                        })
            }
        })
        return binding.root
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
        return valid
    }

    private fun handleError(exception: Exception) {
        val errorMessage: String
        when (exception::class.java) {
            FirebaseAuthInvalidCredentialsException::class.java -> {
                errorMessage =
                    getString(R.string.FirebaseAuthInvalidCredentialsException)
            }
            else -> {
                errorMessage = exception.localizedMessage
            }
        }


        Toast.makeText(activity, errorMessage, Toast.LENGTH_SHORT).show()
    }
}