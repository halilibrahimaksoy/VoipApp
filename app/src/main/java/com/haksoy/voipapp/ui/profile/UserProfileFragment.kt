package com.haksoy.voipapp.ui.profile

import User
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.addCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.bumptech.glide.Glide
import com.github.drjacky.imagepicker.ImagePicker
import com.haksoy.voipapp.R
import com.haksoy.voipapp.databinding.UserProfileFragmentBinding
import com.haksoy.voipapp.ui.main.MainActivity
import com.haksoy.voipapp.utlis.Constants
import com.haksoy.voipapp.utlis.Resource


class UserProfileFragment() : Fragment(), View.OnClickListener {

    companion object {
        fun newInstance(status: Status, selectedUser: User? = null) = UserProfileFragment().apply {
            arguments = bundleOf(
                Constants.UserProfileFragmentReason to status,
                Constants.UserProfileFragmentSelectedUser to selectedUser
            )
        }
    }

    enum class Status {
        REGISTRATION,
        AUTH_USER,
        OTHER_USER
    }

    private lateinit var binding: UserProfileFragmentBinding
    private lateinit var _user: User
    private lateinit var selectedUser: User
    private val viewModel by lazy {
        ViewModelProviders.of(this).get(UserProfileViewModel::class.java)
    }
    private var editMode: Boolean = false
    private var newImageUri: Uri? = null
    private lateinit var reasonStatus: Status
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = UserProfileFragmentBinding.inflate(layoutInflater, container, false)

        if (activity is AppCompatActivity) {
            (activity as AppCompatActivity).setSupportActionBar(binding.toolbar)
        }
        (activity as AppCompatActivity).supportActionBar?.title = ""

        setEditMode()
        binding.imageView.setOnClickListener(View.OnClickListener {
            if (editMode)
                pickImage()
        })

        binding.btnCancel.setOnClickListener(this)
        binding.btnEdit.setOnClickListener(this)
        binding.btnSave.setOnClickListener(this)

        return binding.root
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.get(Constants.UserProfileFragmentReason)?.let { reasonStatus = it as Status }
        arguments?.get(Constants.UserProfileFragmentSelectedUser)?.let { selectedUser = it as User }

        if (reasonStatus == Status.REGISTRATION)
            editMode = true


        setHasOptionsMenu(true)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        if (reasonStatus == Status.AUTH_USER || reasonStatus == Status.REGISTRATION) {
            viewModel.currentUser.observe(viewLifecycleOwner, Observer {
                it?.let {
                    fillUserData(it)
                    _user = it
                }
            })
            binding.txtEmail.text = viewModel.getEmail()
            _user = User(viewModel.getUid(), viewModel.getEmail())
            viewModel.fetchUserDate(viewModel.getUid())
        } else if (reasonStatus == Status.OTHER_USER) {
            fillUserData(selectedUser)
        }

    }

    private fun fillUserData(user: User) {
        if (user.profileImage != null)
            showProfileImage(user.profileImage!!)
        if (reasonStatus != Status.OTHER_USER && user.email != null)
            binding.txtEmail.text = user.email
        binding.txtFullName.text = user.name
        binding.txtInfo.text = user.info

        binding.txtFullName2.setText(user.name)
        binding.txtInfo2.setText(user.info)
    }

    private fun pickImage() {
        ImagePicker.with(this)
            .compress(1024)
            .crop()//Final image size will be less than 1 MB(Optional)
            .maxResultSize(
                1080,
                1080
            ) //Final image resolution will be less than 1080 x 1080(Optional)
            .start { resultCode, data ->
                if (resultCode == Activity.RESULT_OK) {
                    newImageUri = data?.data!!
                    showProfileImage(newImageUri.toString())
                    _user.profileImage = newImageUri.toString()
                } else if (resultCode == ImagePicker.RESULT_ERROR) {
                    Toast.makeText(context, ImagePicker.getError(data), Toast.LENGTH_SHORT)
                        .show()
                }
            }
    }

    private fun validateForm(): Boolean {
        var valid = true

        val email = binding.txtFullName2.text.toString()
        if (TextUtils.isEmpty(email)) {
            binding.txtFullName2.error = "Required."
            valid = false
        } else {
            binding.txtFullName2.error = null
        }


        return valid
    }

    private fun updateUserProfile() {
        if (validateForm()) {
            viewModel.updateUserProfile(_user).observe(viewLifecycleOwner, Observer {
                if (it.status == Resource.Status.SUCCESS) {
                    updateUserProfileCompleted()
                } else if (it.status == Resource.Status.ERROR) {
                    Toast.makeText(activity, it.message, Toast.LENGTH_SHORT).show()
                }
            })
        }
    }

    private fun showProfileImage(currentImageReferance: String) {
        Glide.with(binding.root /* context */)
            .load(currentImageReferance)
            .circleCrop()
            .into(binding.imageView)

    }

    private fun setEditMode() {
        optimizeMenuForStatus()
        if (editMode) {
            binding.rltvEditOn.visibility = View.VISIBLE
            binding.rltvEditOff.visibility = View.GONE
            binding.imageView.isClickable = true
            binding.lnrEdit.visibility = View.VISIBLE
            binding.lnrShow.visibility = View.GONE
            fillEditFields()
        } else {
            binding.rltvEditOn.visibility = View.GONE
            binding.rltvEditOff.visibility = View.VISIBLE
            binding.imageView.isClickable = false
            binding.lnrEdit.visibility = View.GONE
            binding.lnrShow.visibility = View.VISIBLE
        }
    }

    private fun optimizeMenuForStatus() {
        when (reasonStatus) {
            Status.REGISTRATION -> {
                binding.btnCancel.visibility = View.GONE
            }
            Status.OTHER_USER -> {
                binding.btnEdit.visibility = View.GONE
                binding.btnSave.visibility = View.GONE
                binding.btnCancel.visibility = View.GONE
            }
            Status.AUTH_USER -> {
                binding.btnEdit.visibility = View.VISIBLE
                binding.btnSave.visibility = View.VISIBLE
                binding.btnCancel.visibility = View.VISIBLE
            }
        }
    }

    private fun fillEditFields() {
        binding.txtFullName2.setText(binding.txtFullName.text)
        binding.txtInfo2.setText(binding.txtInfo.text)
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.btnEdit -> {
                editMode = true
                setEditMode()
            }
            R.id.btnSave -> {
                setNewUserData()
                updateUserProfile()
            }
            R.id.btnCancel -> {
                updateUserProfileCompleted()
            }
        }
    }

    private fun updateUserProfileCompleted() {
        editMode = false
        setEditMode()
        viewModel.fetchUserDate(viewModel.getUid())

        if (reasonStatus == Status.REGISTRATION) {
            activity?.startActivity(Intent(context, MainActivity::class.java))
            activity?.finish()
        }
    }

    private fun setNewUserData() {
        _user.name = binding.txtFullName2.text.toString()
        _user.info = binding.txtInfo2.text.toString()
    }
}