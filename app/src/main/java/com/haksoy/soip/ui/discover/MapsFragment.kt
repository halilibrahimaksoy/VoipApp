package com.haksoy.soip.ui.discover

import android.Manifest
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
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
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.snackbar.Snackbar
import com.haksoy.soip.BuildConfig
import com.haksoy.soip.R
import com.haksoy.soip.data.user.User
import com.haksoy.soip.databinding.FragmentMapsBinding
import com.haksoy.soip.ui.main.SharedViewModel
import com.haksoy.soip.utlis.*

private const val TAG = "SoIP:MapsFragment"

class MapsFragment : Fragment() {

    companion object {

        fun newInstance(): MapsFragment {
            return MapsFragment()
        }
    }

    private lateinit var binding: FragmentMapsBinding
    lateinit var mapFragment: SupportMapFragment
    private val sharedViewModel: SharedViewModel by activityViewModels()
    private val viewModel: MapsViewModel by activityViewModels()

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
                            Constants.REQUEST_FINE_LOCATION_PERMISSIONS_REQUEST_CODE,
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
                    Constants.REQUEST_BACKGROUND_LOCATION_PERMISSIONS_REQUEST_CODE,
                    backgroundRationalSnackbar
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
                            Constants.REQUEST_FINE_LOCATION_PERMISSIONS_REQUEST_CODE
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
                            Constants.REQUEST_BACKGROUND_LOCATION_PERMISSIONS_REQUEST_CODE
                    )
                }
    }

    @SuppressLint("MissingPermission")
    private fun updateMap() {
        context?.let { it1 -> ProgressHelper.getInstance().showLoading(it1) }
        mapFragment.getMapAsync { it ->
            ProgressHelper.getInstance().hideLoading()
            it.isMyLocationEnabled = true
            it.uiSettings.isMyLocationButtonEnabled = false
            binding.myLocation.setOnClickListener { view ->
                moveToCurrentLocation(it)
            }
            moveToCurrentLocation(it)

            it.setMaxZoomPreference(14f)

            sharedViewModel.nearlyUsers.observe(viewLifecycleOwner, Observer { userList ->
                Log.i(TAG, "updateMap  :  nearlyUsers observed")
//                context?.let { it1 -> ProgressHelper.getInstance().showLoading(it1) }
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
//                ProgressHelper.getInstance().hideLoading()
                Log.i(TAG, "updateMap  :  nearlyUsers added to maps")
            })


            it.setOnMarkerClickListener { marker ->
                showUserList(marker.tag.toString())
                true
            }
        }

    }

    private fun moveToCurrentLocation(it: GoogleMap) {
        context?.let { it1 ->
            LocationServices.getFusedLocationProviderClient(it1).lastLocation.addOnSuccessListener { location ->
                if(location!=null)
                it.animateCamera(
                        CameraUpdateFactory.newLatLngZoom(
                                LatLng(
                                        location.latitude,
                                        location.longitude
                                ), 14f
                        )
                )
            }
        }
    }

    private fun showUserList(selectedUserUid: String) {
        Log.i(TAG, "showUserList  :  selectedUserList added new value")
        sharedViewModel.selectedUserList = sharedViewModel.nearlyUsers.value as ArrayList<User>
        Log.i(TAG, "showUserList  :  selectedUserUid added new value")
        sharedViewModel.selectedUserUid.postValue(selectedUserUid)
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
        val permissionDeniedExplanation =
                if (requestCode == Constants.REQUEST_FINE_LOCATION_PERMISSIONS_REQUEST_CODE) {
                    R.string.fine_permission_denied_explanation
                } else {
                    R.string.background_permission_denied_explanation
                }

        val settingsSnackbar = Snackbar.make(
                binding.root,
                permissionDeniedExplanation,
                Snackbar.LENGTH_LONG
        )
                .setAction(R.string.settings) {
                    // Build intent that displays the App settings screen.
                    val intent = Intent()
                    intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                    val uri = Uri.fromParts(
                            "package",
                            BuildConfig.APPLICATION_ID,
                            null
                    )
                    intent.data = uri
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    startActivity(intent)
                }
        if (requestCode == Constants.REQUEST_FINE_LOCATION_PERMISSIONS_REQUEST_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                viewModel.startLocationUpdates()
                prepareUi()
            } else {
                settingsSnackbar.show()
            }
            if (grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                context?.putPreferencesBoolean(
                        getString(R.string.enable_background_location_key),
                        true
                )
            }

        } else if (requestCode == Constants.REQUEST_BACKGROUND_LOCATION_PERMISSIONS_REQUEST_CODE) {
            when (PackageManager.PERMISSION_GRANTED) {
                grantResults[0] -> {
                    context?.putPreferencesBoolean(
                            getString(R.string.enable_background_location_key),
                            true
                    )
                }
                else -> settingsSnackbar.show()
            }
        }
    }

    private fun prepareUi() {
        sharedViewModel.fetchNearlyUsers()
        updateMap()
        getBackgroundPermission()
    }

    override fun onStart() {
        super.onStart()
        viewModel.startLocationUpdates()
    }

    override fun onStop() {
        super.onStop()
        if (!context?.getPreferencesBoolean(
                        getString(R.string.enable_background_location_key),
                        false
                )!!
        ) {
            viewModel.stopLocationUpdates()
        }
    }

}