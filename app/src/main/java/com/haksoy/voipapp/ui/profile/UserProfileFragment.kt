package com.haksoy.voipapp.ui.profile

import android.app.Activity
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
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.github.drjacky.imagepicker.ImagePicker
import com.haksoy.voipapp.R
import com.haksoy.voipapp.databinding.UserProfileFragmentBinding
import com.haksoy.voipapp.utlis.Resource


class UserProfileFragment() : Fragment(), View.OnClickListener {

    companion object {

        fun newInstance(status: Status) = UserProfileFragment().apply {
            arguments = bundleOf("status" to status)
        }
    }

    enum class Status {
        REGISTRATION,
        AUTH_USER,
        OTHER_USER
    }

    private lateinit var binding: UserProfileFragmentBinding
    private val viewModel by lazy {
        ViewModelProviders.of(this).get(UserProfileViewModel::class.java)
    }
    private var editMode: Boolean = false
    private var newImageUri: Uri? = null
    private lateinit var activeUID: String
    private lateinit var activeEmail: String
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
        arguments?.get("isResistration")?.let { editMode = it as Boolean }
        arguments?.get("activeUID")?.let { activeUID = it as String }
        arguments?.get("activeEmail")?.let { activeEmail = it as String }
        arguments?.get("status")?.let { reasonStatus = it as Status }

        setHasOptionsMenu(true)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel.currentUser.observe(viewLifecycleOwner, Observer {
            it.let {
                if (it.picture != null)
                    showProfileImage(it.picture!!)
                if (it.email != null)
                    binding.txtEmail.text = it.email
                binding.txtFullName.text = it.name
                binding.txtInfo.text = it.info

                binding.txtFullName2.setText(it.name)
                binding.txtInfo2.setText(it.info)
            }
        })

        binding.txtEmail.text = viewModel.getEmail()
        viewModel.fetchUserDate(viewModel.getUid())
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
                    //Image Uri will not be null for RESULT_OK
                    newImageUri = data?.data!!
                    binding.imageView.setImageURI(newImageUri)
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
            viewModel.updateUserProfile(
                newImageUri,
                binding.txtFullName2.text.toString(),
                binding.txtInfo2.text.toString()
            ).observe(viewLifecycleOwner, Observer {
                if (it.status == Resource.Status.SUCCESS) {
                    editMode = false
                    setEditMode()
                    viewModel.fetchUserDate(viewModel.getUid())
                } else if (it.status == Resource.Status.ERROR) {
                    Toast.makeText(activity, it.message, Toast.LENGTH_SHORT).show()
                }
            })
        }
    }

    private fun showProfileImage(currentImageReferance: String) {
        Glide.with(binding.root /* context */)
            .load(currentImageReferance)
            .transform(RoundedCorners(40))
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
            fillEditFileds()
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

    private fun fillEditFileds() {
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
                updateUserProfile()
            }
            R.id.btnCancel -> {
                editMode = false
                setEditMode()
            }
        }
    }
}