package com.haksoy.soip.ui.discover

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.haksoy.soip.location.LocationRepository
import java.util.concurrent.Executors

class MapsViewModel(application: Application) : AndroidViewModel(application) {

    private val locationRepository = LocationRepository.getInstance(
        application.applicationContext,
        Executors.newSingleThreadExecutor()
    )


    fun startLocationUpdates() = locationRepository.startLocationUpdates()

    fun stopLocationUpdates() = locationRepository.stopLocationUpdates()
}