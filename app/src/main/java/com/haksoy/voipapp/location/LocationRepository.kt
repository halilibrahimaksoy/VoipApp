package com.haksoy.voipapp.location

import android.content.Context
import android.util.Log
import androidx.annotation.MainThread
import com.haksoy.voipapp.data.FirebaseDao
import com.haksoy.voipapp.data.entiries.Location
import java.util.concurrent.ExecutorService

private const val TAG = "LocationRepository"

/**
 * Access point for database (MyLocation data) and location APIs (start/stop location updates and
 * checking location update status).
 */
class LocationRepository private constructor(
    private val firebaseDao: FirebaseDao,
    private val myLocationManager: LocationManager,
    private val executor: ExecutorService
) {

    /**
     * Adds list of locations to the database.
     */
    fun addLocation(myLocationEntities: Location) {
        Log.i(TAG, "addLocation  :  new location added")
        executor.execute {
            firebaseDao.addLocation(myLocationEntities)
        }
    }

    /**
     * Subscribes to location updates.
     */
    @MainThread
    fun startLocationUpdates() = myLocationManager.startLocationUpdates()

    /**
     * Un-subscribes from location updates.
     */
    @MainThread
    fun stopLocationUpdates() = myLocationManager.stopLocationUpdates()

    companion object {
        @Volatile
        private var INSTANCE: LocationRepository? = null

        fun getInstance(context: Context, executor: ExecutorService): LocationRepository {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: LocationRepository(
                    FirebaseDao.getInstance(),
                    LocationManager.getInstance(context),
                    executor
                )
                    .also { INSTANCE = it }
            }
        }
    }
}
