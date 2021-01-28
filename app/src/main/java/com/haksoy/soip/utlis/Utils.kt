package com.haksoy.soip.utlis

import android.Manifest
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.preference.PreferenceManager
import com.google.android.material.snackbar.Snackbar


/**
 * Helper functions to simplify permission checks/requests.
 */
fun Context.hasPermission(permission: String): Boolean {

    // Background permissions didn't exit prior to Q, so it's approved by default.
    if (permission == Manifest.permission.ACCESS_BACKGROUND_LOCATION &&
        android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.Q
    ) {
        return true
    }

    return ActivityCompat.checkSelfPermission(this, permission) ==
            PackageManager.PERMISSION_GRANTED
}

fun Context.putPreferencesBoolean(key: String, value: Boolean) {
    val preferences = PreferenceManager.getDefaultSharedPreferences(this)
    with(preferences.edit()) {
        putBoolean(key, true)
        commit()
    }
}

fun Context.getPreferencesBoolean(key: String, defaultValue: Boolean): Boolean {
    val preferences = PreferenceManager.getDefaultSharedPreferences(this)
    return preferences.getBoolean(key, defaultValue)
}

fun <T> LiveData<T>.observeOnce(observer: (T) -> Unit) {
    observeForever(object : Observer<T> {
        override fun onChanged(value: T) {
            observer(value)
            removeObserver(this)
        }
    })
}

fun <T> LiveData<T>.observeOnce(observer: Observer<T>) {
    observeForever(object : Observer<T> {
        override fun onChanged(t: T?) {
            observer.onChanged(t)
            removeObserver(this)
        }
    })
}

fun <T> LiveData<T>.observeOnce(lifecycleOwner: LifecycleOwner, observer: Observer<T>) {
    observe(lifecycleOwner, object : Observer<T> {
        override fun onChanged(t: T?) {
            observer.onChanged(t)
            removeObserver(this)
        }
    })
}

fun <T> LiveData<T>.observeOnce(lifecycleOwner: LifecycleOwner, observer: (T) -> Unit) {
    observe(lifecycleOwner, object : Observer<T> {
        override fun onChanged(value: T) {
            observer(value)
            removeObserver(this)
        }
    })
}

fun Context.startInstagram(username: String) {
    val i = Intent(Intent.ACTION_VIEW, Uri.parse("http://instagram.com/_u/$username"))
    i.setPackage("com.instagram.android")

    try {
        startActivity(i)
    } catch (e: ActivityNotFoundException) {
        startActivity(
            Intent(
                Intent.ACTION_VIEW,
                Uri.parse("http://instagram.com/$username")
            )
        )
    }
}

fun Context.startTwitter(username: String) {
    try {
        startActivity(
            Intent(
                Intent.ACTION_VIEW,
                Uri.parse("twitter://user?screen_name=$username")
            )
        )
    } catch (e: Exception) {
        startActivity(
            Intent(
                Intent.ACTION_VIEW,
                Uri.parse("https://twitter.com/$username")
            )
        )
    }
}

fun Context.startFacebook(username: String) {
    var facebookUrl: String = ""
    val FACEBOOK_URL = "https://www.facebook.com/$username"
    try {
        val versionCode = packageManager.getPackageInfo("com.facebook.katana", 0).versionCode
        if (versionCode >= 3002850) { //newer versions of fb app
            facebookUrl = "fb://facewebmodal/f?href=$FACEBOOK_URL"
        } else { //older versions of fb app
            facebookUrl = "fb://page/$username"
        }

        val facebookIntent = Intent(Intent.ACTION_VIEW, Uri.parse(facebookUrl))
        startActivity(facebookIntent)
    } catch (e: java.lang.Exception) {
        startActivity(
            Intent(
                Intent.ACTION_VIEW,
                Uri.parse("https://www.facebook.com/$username")
            )
        )
    }

}

fun Fragment.requestPermissionWithRationale(
    permission: String,
    requestCode: Int,
    snackbar: Snackbar
) {
    val provideRationale = shouldShowRequestPermissionRationale(permission)

    if (provideRationale) {
        snackbar.show()
    } else {
        requestPermissions(arrayOf(permission), requestCode)
    }
}

fun Fragment.requestPermissionsWithRationale(
    permissions: Array<String>,
    requestCode: Int,
    snackbar: Array<Snackbar>
) {
    if (shouldShowRequestPermissionRationale(permissions[0])) {
        snackbar[0].show()
    } else if (shouldShowRequestPermissionRationale(permissions[1])) {
        snackbar[1].show()
    } else {
        requestPermissions(permissions, requestCode)
    }
}

