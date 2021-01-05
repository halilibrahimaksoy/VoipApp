package com.haksoy.voipapp.ui.discover

import User
import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.os.StrictMode
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.target.SimpleTarget
import com.bumptech.glide.request.transition.Transition
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.haksoy.voipapp.R


class MapsFragment : Fragment() {
    private val viewModel by lazy {
        ViewModelProviders.of(this).get(MapsViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_maps, container, false)
    }

    @SuppressLint("MissingPermission")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        viewModel.fetchNearlyUsers()
        mapFragment?.getMapAsync {
            it.isMyLocationEnabled = true
            val height = activity?.windowManager?.defaultDisplay?.height
            it.setPadding(0, height?.times(0.8)?.toInt()!!, 0, 0)

            viewModel.nearlyUsers.observe(viewLifecycleOwner, Observer { userList ->
                Toast.makeText(activity, "Updating nearby users", Toast.LENGTH_SHORT).show()
                it.clear()
                val move =
                    LatLng(userList[0].location.latitude, userList[0].location.longitude)
                it.animateCamera(CameraUpdateFactory.newLatLngZoom(move, 20f))
                for (user in userList) {
                    Glide.with(activity?.applicationContext!!)
                        .asBitmap()
                        .circleCrop()
                        .load(user.picture)
                        .diskCacheStrategy(DiskCacheStrategy.NONE)
                        .skipMemoryCache(true)
                        .apply(RequestOptions().override(100, 100))
                        .into(object : CustomTarget<Bitmap?>() {
                            override fun onResourceReady(
                                resource: Bitmap,
                                transition: Transition<in Bitmap?>?
                            ) {
                                it.addMarker(getMarkerOptions(user))
                                    .setIcon(BitmapDescriptorFactory.fromBitmap(resource))
                            }

                            override fun onLoadFailed(errorDrawable: Drawable?) {
                                super.onLoadFailed(errorDrawable)
                                it.addMarker(getMarkerOptions(user))
                                    .setIcon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_profile))
                            }

                            override fun onLoadCleared(placeholder: Drawable?) {
                                TODO("Not yet implemented")
                            }

                        })

                }
            })
        }
    }

    private fun getMarkerOptions(user: User): MarkerOptions? {
        return MarkerOptions().position(
            LatLng(
                user.location.latitude,
                user.location.longitude
            )
        ).title(user.name)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        val policy =
            StrictMode.ThreadPolicy.Builder().permitAll().build()
        StrictMode.setThreadPolicy(policy)
        super.onCreate(savedInstanceState)
    }

    companion object {

        @JvmStatic
        fun newInstance(): MapsFragment {
            return MapsFragment()
        }
    }
}