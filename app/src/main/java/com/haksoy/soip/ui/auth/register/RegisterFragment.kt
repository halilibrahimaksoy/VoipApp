package com.haksoy.soip.ui.auth.register

import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import com.haksoy.soip.R
import com.haksoy.soip.databinding.FragmentRegisterBinding
import com.haksoy.soip.ui.auth.AuthenticationViewModel
import com.haksoy.soip.ui.profile.UserProfileFragment
import com.haksoy.soip.utlis.Constants
import com.haksoy.soip.utlis.Resource
import com.haksoy.soip.utlis.observeWithProgress

class RegisterFragment : Fragment() {
    private lateinit var binding: FragmentRegisterBinding
    private val viewModel by lazy {
        ViewModelProviders.of(this).get(AuthenticationViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentRegisterBinding.inflate(inflater, container, false)

        binding.btnRegister.setOnClickListener(View.OnClickListener {
            if (validateForm()) {
                createAccount()
            }
        })
        return binding.root
    }

    private fun createAccount() {
        context?.let {
            viewModel.createAccount(
                    binding.txtEmail.text.toString(),
                    binding.txtPassword.text.toString()
        ).observeWithProgress(it,viewLifecycleOwner,
                    Observer {
                        if (it.status == Resource.Status.SUCCESS) {
                            singIn()
                        } else if (it.status == Resource.Status.ERROR) {
                            it.data?.let { it1 -> handleError(it1) }
                        }

                    })
        }
    }

    private fun singIn() {
        context?.let {
            viewModel.signIn(
                    binding.txtEmail.text.toString(),
                    binding.txtPassword.text.toString()
        ).observeWithProgress(it,viewLifecycleOwner,
                    Observer {
                        if (it.status == Resource.Status.SUCCESS) {

                            findNavController().navigate(
                                    R.id.action_registerFragment_to_userProfileFragment,
                                    bundleOf(Constants.UserProfileFragmentReason to UserProfileFragment.Status.REGISTRATION)
                            )
                        } else if (it.status == Resource.Status.ERROR) {
                            it.data?.let { it1 -> handleError(it1) }
                        }
                    })
        }
    }

    private fun validateForm(): Boolean {
        var valid = true

        val email = binding.txtEmail.text.toString()
        if (TextUtils.isEmpty(email) || !Constants.EMAIL_ADDRESS_PATTERN.matcher(email).matches()) {
            binding.txtEmail.error = getString(R.string.required)
            valid = false
        } else {
            binding.txtEmail.error = null
        }

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
        val password2 = binding.txtPassword2.text.toString()
        when {
            TextUtils.isEmpty(password2) -> {
                binding.txtPassword2.error = getString(R.string.required)
                valid = false
            }
            password.count() < Constants.MIN_PASSWORD_CHARACTER_COUNT -> {
                binding.txtPassword.error = getString(R.string.should_be_min_6)
                valid = false
            }
            else -> {
                binding.txtPassword2.error = null
            }
        }

        if (password != password2) {
            binding.txtPassword.error = getString(R.string.should_be_same)
            binding.txtPassword2.error = getString(R.string.should_be_same)
            Toast.makeText(activity, getString(R.string.deosnt_match), Toast.LENGTH_SHORT).show()
            valid = false
        }


        return valid
    }

    private fun handleError(exception: Exception) {
        val errorMessage: String
        when (exception::class.java) {
            FirebaseAuthWeakPasswordException::class.java -> {
                errorMessage =
                    getString(R.string.FirebaseAuthWeakPasswordException)
                showError(binding.txtPassword, binding.txtPassword2)
            }
            FirebaseAuthInvalidCredentialsException::class.java -> {
                errorMessage =
                    getString(R.string.FirebaseAuthInvalidCredentialsException)
                showError(binding.txtEmail)
            }
            FirebaseAuthUserCollisionException::class.java -> {
                errorMessage =
                    getString(R.string.FirebaseAuthUserCollisionException)
                showError(binding.txtEmail)
            }
            else -> {
                errorMessage = exception.localizedMessage
            }
        }


        Toast.makeText(activity, errorMessage, Toast.LENGTH_SHORT).show()
    }

    private fun showError(vararg editText: EditText) {
        for (item in editText) {
            item.error = getString(R.string.required)
        }
    }
}