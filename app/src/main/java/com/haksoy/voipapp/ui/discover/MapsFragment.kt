package com.haksoy.voipapp.ui.discover

import User
import android.Manifest
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.snackbar.Snackbar
import com.haksoy.voipapp.R
import com.haksoy.voipapp.databinding.FragmentMapsBinding
import com.haksoy.voipapp.ui.userlist.UserListViewModel
import com.haksoy.voipapp.utlis.hasPermission
import com.haksoy.voipapp.utlis.requestPermissionWithRationale
import com.haksoy.voipapp.utlis.requestPermissionsWithRationale

private const val TAG = "MapsFragment"

class MapsFragment : Fragment() {

    companion object {
        private const val REQUEST_FINE_LOCATION_PERMISSIONS_REQUEST_CODE = 34
        private const val REQUEST_BACKGROUND_LOCATION_PERMISSIONS_REQUEST_CODE = 56

        fun newInstance(): MapsFragment {
            return MapsFragment()
        }
    }

    private lateinit var binding: FragmentMapsBinding
    lateinit var mapFragment: SupportMapFragment
    private val userListViewModel: UserListViewModel by activityViewModels()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMapsBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mapFragment = (childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?)!!

        if (activity?.hasPermission(Manifest.permission.ACCESS_FINE_LOCATION)!!) {
            prepareUi()
        } else {
            getPermissionRequest()
        }

    }

    private fun getPermissionRequest() {
        AlertDialog.Builder(activity)
            .setIcon(R.mipmap.ic_launcher_round)
            .setTitle(R.string.app_name)
            .setMessage(R.string.fine_location_permission_dialog)
            .setCancelable(false)
            .setPositiveButton(R.string.ok) { _, _ ->
                requestPermissionsWithRationale(
                    arrayOf(
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_BACKGROUND_LOCATION
                    ),
                    REQUEST_FINE_LOCATION_PERMISSIONS_REQUEST_CODE,
                    arrayOf(
                        fineLocationRationalSnackbar,
                        backgroundRationalSnackbar
                    )
                )
            }.show()

    }

    private fun getBackgroundPermission() {
        if (!activity?.hasPermission(Manifest.permission.ACCESS_BACKGROUND_LOCATION)!!)
            requestPermissionWithRationale(
                Manifest.permission.ACCESS_BACKGROUND_LOCATION,
                REQUEST_BACKGROUND_LOCATION_PERMISSIONS_REQUEST_CODE, backgroundRationalSnackbar
            )
    }

    private val fineLocationRationalSnackbar by lazy {
        Snackbar.make(
            binding.root,
            R.string.fine_location_permission_rationale,
            Snackbar.LENGTH_LONG
        )
            .setAction(R.string.ok) {
                requestPermissions(
                    arrayOf(
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_BACKGROUND_LOCATION
                    ),
                    REQUEST_FINE_LOCATION_PERMISSIONS_REQUEST_CODE
                )
            }
    }

    private val backgroundRationalSnackbar by lazy {
        Snackbar.make(
            binding.root,
            R.string.background_location_permission_rationale,
            Snackbar.LENGTH_LONG
        )
            .setAction(R.string.ok) {
                requestPermissions(
                    arrayOf(Manifest.permission.ACCESS_BACKGROUND_LOCATION),
                    REQUEST_BACKGROUND_LOCATION_PERMISSIONS_REQUEST_CODE
                )
            }
    }

    @SuppressLint("MissingPermission")
    private fun updateMap() {
        mapFragment.getMapAsync { it ->


            it.isMyLocationEnabled = true
            it.uiSettings.isMyLocationButtonEnabled = false
            binding.myLocation.setOnClickListener { view ->
                it.myLocation?.let { location ->
                    it.animateCamera(
                        CameraUpdateFactory.newLatLngZoom(
                            LatLng(
                                location.latitude,
                                location.longitude
                            ), 15f
                        )
                    )

                }
            }


            userListViewModel.nearlyUsers.observe(viewLifecycleOwner, Observer { userList ->
                Log.i(TAG, "userListViewModel  :  nearlyUsers observed")
                it.clear()
                for (user in userList) {
                    Glide.with(activity?.applicationContext!!)
                        .asBitmap()
                        .circleCrop()
                        .load(user.profileImage)
                        .apply(RequestOptions().override(100, 100))
                        .into(object : CustomTarget<Bitmap?>() {
                            override fun onResourceReady(
                                resource: Bitmap,
                                transition: Transition<in Bitmap?>?
                            ) {
                                it.addMarker(getMarkerOptions(user)).apply {
                                    tag = user.uid
                                }.setIcon(BitmapDescriptorFactory.fromBitmap(resource))
                            }

                            override fun onLoadFailed(errorDrawable: Drawable?) {
                                super.onLoadFailed(errorDrawable)
                                it.addMarker(getMarkerOptions(user)).apply {
                                    tag = user.uid
                                }.setIcon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_profile))
                            }

                            override fun onLoadCleared(placeholder: Drawable?) {
                                TODO("Not yet implemented")
                            }
                        })
                }
            })


            it.setOnMarkerClickListener { marker ->
                showUserList(marker.tag.toString())
                true
            }
        }

    }

    private fun showUserList(selectedUserUid: String) {
        Log.i(TAG, "userListViewModel  :  selectedUserList added new value")
        userListViewModel.selectedUserList = userListViewModel.nearlyUsers.value as ArrayList<User>
        Log.i(TAG, "userListViewModel  :  selectedUserUid added new value")
        userListViewModel.selectedUserUid.postValue(selectedUserUid)
    }

    private fun getMarkerOptions(user: User): MarkerOptions? {
        return MarkerOptions().position(
            LatLng(
                user.location.latitude,
                user.location.longitude
            )
        ).title(user.name)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        Log.d(TAG, "onRequestPermissionResult")
        if (requestCode == REQUEST_FINE_LOCATION_PERMISSIONS_REQUEST_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                prepareUi()
            }
        }
    }

    private fun prepareUi() {
        userListViewModel.fetchNearlyUsers()
        updateMap()
        getBackgroundPermission()
    }


}