package com.haksoy.soip.ui.auth.login

import android.app.AlertDialog
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
import com.haksoy.soip.R
import com.haksoy.soip.databinding.FragmentLoginBinding
import com.haksoy.soip.ui.auth.AuthenticationViewModel
import com.haksoy.soip.ui.main.MainActivity
import com.haksoy.soip.utlis.Constants
import com.haksoy.soip.utlis.Resource

class LoginFragment : Fragment() {

    private lateinit var binding: FragmentLoginBinding
    private val viewModel by lazy {
        ViewModelProviders.of(this).get(AuthenticationViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentLoginBinding.inflate(inflater, container, false)

        binding.btnRegister.setOnClickListener(View.OnClickListener {
            findNavController().navigate(R.id.action_loginFragment_to_registerFragment)
        })
        binding.btnLogin.setOnClickListener(View.OnClickListener {
            if (validateEmail() && validatePassword()) {
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
        binding.btnForgetPassword.setOnClickListener {
            if (validateEmail())
                viewModel.forgetPassword(binding.txtEmail.text.toString())
                    .observe(viewLifecycleOwner,
                        Observer {
                            if (it.status == Resource.Status.SUCCESS) {
                                showAlertForgetPassword(binding.txtEmail.text.toString())
                            } else if (it.status == Resource.Status.ERROR) {
                                it.data?.let { it1 -> handleError(it1) }
                            }
                        })
        }
        return binding.root
    }

    private fun validateEmail(): Boolean {
        var valid = true

        val email = binding.txtEmail.text.toString()
        if (TextUtils.isEmpty(email) || !Constants.EMAIL_ADDRESS_PATTERN.matcher(email).matches()) {
            binding.txtEmail.error = getString(R.string.required)
            valid = false
        } else {
            binding.txtEmail.error = null
        }
        return valid
    }

    private fun showAlertForgetPassword(email: String) {
        AlertDialog.Builder(context).setTitle(getString(R.string.app_name))
            .setMessage(getString(R.string.forget_password_message, email)).show()
    }

    private fun validatePassword(): Boolean {
        var valid = true
        val password = binding.txtPassword.text.toString()
        when {
            TextUtils.isEmpty(password) -> {
                binding.txtPassword.error = getString(R.string.required)
                valid = false
            }
            password.count() < Constants.MIN_PASSWORD_CHARACTER_COUNT -> {
                binding.txtPassword.error = getString(R.string.should_be_min_6)
                valid = false
            }
            else -> {
                binding.txtPassword.error = null
            }
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


        Toast.makeText(activity, errorMessage, Toast.LENGTH_LONG).show()
    }
}