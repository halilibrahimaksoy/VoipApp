package com.haksoy.voipapp.ui.auth.register

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
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import com.haksoy.voipapp.R
import com.haksoy.voipapp.databinding.RegisterFragmentBinding
import com.haksoy.voipapp.ui.auth.AuthenticationViewModel
import com.haksoy.voipapp.ui.profile.UserProfileFragment
import com.haksoy.voipapp.utlis.Resource

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
                    it.data?.let { it1 -> handleError(it1) }
                }

            })
    }

    private fun singIn() {
        viewModel.signIn(
            binding.txtEmail.text.toString(),
            binding.txtPassword.text.toString()
        ).observe(viewLifecycleOwner,
            Observer {
                if (it.status == Resource.Status.SUCCESS) {

                    findNavController().navigate(
                        R.id.action_registerFragment_to_userProfileFragment,
                        bundleOf("isResistration" to true,"status" to UserProfileFragment.Status.REGISTRATION)
                    )
                } else if (it.status == Resource.Status.ERROR) {
                    it.data?.let { it1 -> handleError(it1) }
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
        val password2 = binding.txtPassword2.text.toString()
        if (TextUtils.isEmpty(password2)) {
            binding.txtPassword2.error = "Required."
            valid = false
        } else {
            binding.txtPassword2.error = null
        }

        if (!password.equals(password2)) {
            binding.txtPassword.error = "Should be same."
            binding.txtPassword2.error = "Should be same."
            Toast.makeText(activity, "Passwords doesn't match", Toast.LENGTH_SHORT).show()
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
            item.error = "Required."
        }
    }
}