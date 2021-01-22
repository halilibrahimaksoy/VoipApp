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
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.bumptech.glide.Glide
import com.github.drjacky.imagepicker.ImagePicker
import com.haksoy.voipapp.R
import com.haksoy.voipapp.databinding.FragmentUserProfileBinding
import com.haksoy.voipapp.ui.main.MainActivity
import com.haksoy.voipapp.utlis.*


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

    private lateinit var binding: FragmentUserProfileBinding
    private lateinit var _user: User
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
        binding = FragmentUserProfileBinding.inflate(layoutInflater, container, false)

        if (activity is AppCompatActivity) {
            (activity as AppCompatActivity).setSupportActionBar(binding.toolbar)
        }
        (activity as AppCompatActivity).supportActionBar?.title = ""

        optimizeMenuForStatus()
        setEditMode()

        binding.imageView.setOnClickListener(this)
        binding.btnCancel.setOnClickListener(this)
        binding.btnEdit.setOnClickListener(this)
        binding.btnSave.setOnClickListener(this)
        binding.btnBack.setOnClickListener(this)
        binding.btnSend.setOnClickListener(this)
        binding.imgInstagram.setOnClickListener(this)
        binding.imgFacebook.setOnClickListener(this)
        binding.imgTwitter.setOnClickListener(this)

        return binding.root
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.get(Constants.UserProfileFragmentReason)?.let { reasonStatus = it as Status }
        arguments?.get(Constants.UserProfileFragmentSelectedUser)?.let { _user = it as User }

        if (reasonStatus == Status.REGISTRATION)
            editMode = true


        setHasOptionsMenu(true)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        if (reasonStatus == Status.AUTH_USER || reasonStatus == Status.REGISTRATION) {

            binding.txtEmail.text = viewModel.getEmail()
            _user = User(viewModel.getUid(), viewModel.getEmail())
            viewModel.fetchUserDate(viewModel.getUid())
            viewModel.currentUser.observe(viewLifecycleOwner, Observer {
                it?.let {
                    fillUserData(it)
                    _user = it
                }
            })
        } else if (reasonStatus == Status.OTHER_USER) {
            fillUserData(_user)
        }

    }

    private fun fillUserData(user: User) {
        if (user.profileImage != null)
            showProfileImage(user.profileImage!!)
        if (reasonStatus != Status.OTHER_USER && user.email != null)
            binding.txtEmail.text = user.email
        binding.txtFullName.text = user.name
        binding.txtInfo.text = user.info
        if (!user.socialMedia.instagram.isNullOrEmpty())
            binding.imgInstagram.visibility = View.VISIBLE

        if (!user.socialMedia.facebook.isNullOrEmpty())
            binding.imgFacebook.visibility = View.VISIBLE

        if (!user.socialMedia.twitter.isNullOrEmpty())
            binding.imgTwitter.visibility = View.VISIBLE
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
        if (editMode) {
            if (reasonStatus == Status.AUTH_USER)
                binding.btnCancel.visibility = View.VISIBLE
            binding.btnSave.visibility = View.VISIBLE
            binding.btnEdit.visibility = View.GONE
            binding.imageView.isClickable = true
            binding.lnrEdit.visibility = View.VISIBLE
            binding.lnrShow.visibility = View.GONE
            fillEditFields()
        } else {
            binding.btnSave.visibility = View.GONE
            binding.btnCancel.visibility = View.GONE
            binding.btnEdit.visibility = View.VISIBLE
            binding.imageView.isClickable = false
            binding.lnrEdit.visibility = View.GONE
            binding.lnrShow.visibility = View.VISIBLE
        }
    }

    private fun optimizeMenuForStatus() {
        when (reasonStatus) {
            Status.REGISTRATION -> {
                binding.btnCancel.visibility = View.GONE
                binding.rltvEdit.visibility = View.VISIBLE
                binding.rltvPresent.visibility = View.GONE
            }
            Status.OTHER_USER -> {
                binding.rltvEdit.visibility = View.GONE
                binding.rltvPresent.visibility = View.VISIBLE
            }
            Status.AUTH_USER -> {
                binding.rltvEdit.visibility = View.VISIBLE
                binding.rltvPresent.visibility = View.GONE
            }
        }
    }

    private fun fillEditFields() {
        binding.txtFullName2.setText(binding.txtFullName.text)
        binding.txtInfo2.setText(binding.txtInfo.text)
        binding.txtInstagram2.setText(_user.socialMedia.instagram)
        binding.txtFacebook2.setText(_user.socialMedia.facebook)
        binding.txtTwitter2.setText(_user.socialMedia.twitter)
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
            R.id.btnBack -> {
                activity?.onBackPressed()
            }
            R.id.imageView -> {
                if (editMode)
                    pickImage()
            }
            R.id.imgInstagram -> {
                _user.socialMedia.instagram?.let { activity?.startInstagram(it) }
            }
            R.id.imgTwitter -> {
                _user.socialMedia.twitter?.let { activity?.startTwitter(it) }
            }
            R.id.imgFacebook -> {
                _user.socialMedia.facebook?.let { activity?.startFacebook(it) }
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
        _user.socialMedia.instagram = binding.txtInstagram2.text.toString()
        _user.socialMedia.twitter = binding.txtTwitter2.text.toString()
        _user.socialMedia.facebook = binding.txtFacebook2.text.toString()
    }
}