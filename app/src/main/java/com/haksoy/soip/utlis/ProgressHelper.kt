package com.haksoy.soip.utlis

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.util.Log
import android.view.Window
import android.widget.ProgressBar
import androidx.appcompat.app.AlertDialog

private const val TAG = "SoIP:ProgressHelper"
class ProgressHelper {
    companion object {
        @Volatile
        private var INSTANCE: ProgressHelper? = null

        fun getInstance(): ProgressHelper {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: ProgressHelper().also { INSTANCE = it }
            }
        }
    }

    private lateinit var dialogBuilder: AlertDialog.Builder
    private lateinit var alertDialog: AlertDialog
    private lateinit var pDialog: ProgressBar


    fun showLoading(context: Context) {
        Log.i(TAG, "showLoading")
        // instantiating the lateint objects
        dialogBuilder = AlertDialog.Builder(context)
        pDialog = ProgressBar(context)

        // setting up the dialog
        dialogBuilder.setCancelable(false)
        dialogBuilder.setView(pDialog)
        alertDialog = dialogBuilder.create()

        // magic of transparent background goes here
        alertDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        // setting the alertDialog's BackgroundDrawable as the color resource of any color with 1% opacity
        alertDialog.window?.setBackgroundDrawable(ColorDrawable(Color.parseColor("#00141414")))

        // finally displaying the Alertdialog containging the ProgressBar
        alertDialog.show()

    }


    fun hideLoading() {
        Log.i(TAG, "hideLoading")
        try {
            if (alertDialog.isShowing) {
                alertDialog.dismiss()
            }
        } catch (e: UninitializedPropertyAccessException) {
            //  Log.d("TAG","AlertDialog UninitializedPropertyAccessException")
        }
    }

}