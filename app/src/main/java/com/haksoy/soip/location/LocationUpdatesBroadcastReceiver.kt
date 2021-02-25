package com.haksoy.soip.location

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.google.android.gms.location.LocationResult
import com.haksoy.soip.data.user.Location
import com.haksoy.soip.utlis.isAppInForeground
import java.util.*
import java.util.concurrent.Executors

private const val TAG = "SoIP:LUBroadcastReceiver"

/**
 * Receiver for handling location updates.
 *
 * For apps targeting API level O and above
 * {@link android.app.PendingIntent#getBroadcast(Context, int, Intent, int)} should be used when
 * requesting location updates in the background. Due to limits on background services,
 * {@link android.app.PendingIntent#getService(Context, int, Intent, int)} should NOT be used.
 *
 *  Note: Apps running on "O" devices (regardless of targetSdkVersion) may receive updates
 *  less frequently than the interval specified in the
 *  {@link com.google.android.gms.location.LocationRequest} when the app is no longer in the
 *  foreground.
 */
class LocationUpdatesBroadcastReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        Log.d(TAG, "onReceive() context:$context, intent:$intent")

        if (intent.action == ACTION_PROCESS_UPDATES) {
            LocationResult.extractResult(intent)?.let { locationResult ->
                val location = locationResult.lastLocation.let {
                    Location(
                        UUID.randomUUID().toString(),
                        latitude = it.latitude,
                        longitude = it.longitude,
                        foreground = context.isAppInForeground(),
                        date = Date(it.time)
                    )
                }

                LocationRepository.getInstance(context,
                    Executors.newSingleThreadExecutor()
                )
                    .addLocation(location)
            }
        }
    }



    companion object {
        const val ACTION_PROCESS_UPDATES =
            "com.haksoy.soip.location.LocationUpdatesBroadcastReceiver" +
                    "PROCESS_UPDATES"
    }
}
