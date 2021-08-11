package com.haksoy.soip.ui

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.os.SystemClock
import android.util.Log
import android.widget.Chronometer
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toFile
import com.cjt2325.cameralibrary.JCameraView
import com.cjt2325.cameralibrary.ResultCodes
import com.cjt2325.cameralibrary.listener.ClickListener
import com.cjt2325.cameralibrary.listener.ErrorListener
import com.cjt2325.cameralibrary.listener.JCameraListener
import com.cjt2325.cameralibrary.listener.RecordStartListener
import com.cjt2325.cameralibrary.util.DeviceUtil
import com.github.drjacky.imagepicker.ImagePicker
import com.haksoy.soip.R
import com.haksoy.soip.data.chat.ChatType
import com.haksoy.soip.utlis.FileUtils
import com.haksoy.soip.utlis.IntentUtils
import me.zhanghai.android.systemuihelper.SystemUiHelper
import java.io.File


class CameraActivity : AppCompatActivity() {
    //max image selectable(when choosing from gallery)
    val MAX_IMAGE_SELECTABLE = 1

    //max video selectable(when choosing from gallery)
    val MAX_VIDEO_SELECTABLE = 1
    val REQUEST_CODE_PICK_FROM_GALLERY = 2323
    private var jCameraView: JCameraView? = null
    private var chronometer: Chronometer? = null
    var uiHelper: SystemUiHelper? = null

    //if the user opens the camera for adding new status we will save the image in the received images folder
    private var isStatus = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_camera)
        chronometer = findViewById(R.id.chronometer)
        uiHelper = SystemUiHelper(
            this,
            SystemUiHelper.LEVEL_IMMERSIVE,
            SystemUiHelper.FLAG_IMMERSIVE_STICKY
        )


        //if the user opens the camera for adding new status we will save the image in the received images folder
        isStatus = intent.hasExtra(IntentUtils.IS_STATUS)
        jCameraView = findViewById(R.id.jcameraview)
        //if it's status we will save the video in received video folder,otherwise we will save it in sent video folder
        jCameraView?.setSaveVideoPath(FileUtils.generateFile(ChatType.SEND_VIDEO)!!.path)
        jCameraView?.setFeatures(JCameraView.BUTTON_STATE_BOTH)
        jCameraView?.setTip(getString(R.string.camera_tip))

        //show pickImage from gallery button if needed
        if (intent.hasExtra(IntentUtils.CAMERA_VIEW_SHOW_PICK_IMAGE_BUTTON)) jCameraView?.showPickImageButton()

        //set media quality
        jCameraView?.setMediaQuality(JCameraView.MEDIA_QUALITY_MIDDLE)
        jCameraView?.setErrorLisenter(object : ErrorListener {
            override fun onError() {
                Log.i("CJT", "camera error")
                val intent = Intent()
                setResult(ResultCodes.CAMERA_ERROR_STATE, intent)
                finish()
            }

            override fun AudioPermissionError() {
                Toast.makeText(
                    this@CameraActivity,
                    R.string.audio_permission_error,
                    Toast.LENGTH_SHORT
                ).show()
            }
        })
        jCameraView?.setJCameraLisenter(object : JCameraListener {
            override fun captureSuccess(bitmap: Bitmap) {
                val outputFile: File? = FileUtils.generateFile(ChatType.SEND_IMAGE)
                FileUtils.convertBitmapToJpeg(bitmap, outputFile!!)
                val intent = Intent()
                intent.putExtra(IntentUtils.EXTRA_PATH_RESULT, outputFile.path)
                intent.putExtra(IntentUtils.EXTRA_FILE_NAME_RESULT, outputFile.name)
                intent.putExtra(IntentUtils.EXTRA_TYPE_RESULT, ChatType.SEND_IMAGE)
                setResult(Activity.RESULT_OK, intent)
                finish()
            }

            override fun recordSuccess(url: String, firstFrame: Bitmap) {
                val intent = Intent()
                intent.putExtra(IntentUtils.EXTRA_PATH_RESULT, url)
                intent.putExtra(IntentUtils.EXTRA_FILE_NAME_RESULT, url)
                intent.putExtra(IntentUtils.EXTRA_TYPE_RESULT, ChatType.SEND_VIDEO)
                setResult(Activity.RESULT_OK, intent)
                finish()
            }

            override fun quit() {}

        })
        jCameraView?.setRecordStartListener(object : RecordStartListener {
            override fun onStart() {
                chronometer?.setBase(SystemClock.currentThreadTimeMillis())
                chronometer?.start()
            }

            override fun onStop() {
                chronometer?.stop()
            }
        })
        jCameraView?.setLeftClickListener(ClickListener { finish() })
        jCameraView?.setPickImageListener(ClickListener { pickImages() })
        Log.i("CJT", DeviceUtil.getDeviceModel())
    }


    private fun pickImages() {
        ImagePicker.with(this).galleryOnly()
            .compress(1024)
            .crop()//Final image size will be less than 1 MB(Optional)
            .maxResultSize(
                1080,
                1080
            ) //Final image resolution will be less than 1080 x 1080(Optional)
            .start { resultCode, data ->
                if (resultCode == Activity.RESULT_OK) {
                    val intent = Intent()
                    intent.putExtra(IntentUtils.EXTRA_PATH_RESULT, data!!.data!!.path)
                    intent.putExtra(IntentUtils.EXTRA_FILE_NAME_RESULT, data.data!!.toFile().name)
                    intent.putExtra(IntentUtils.EXTRA_TYPE_RESULT, ChatType.SEND_IMAGE)
                    setResult(Activity.RESULT_OK, intent)
                    finish()
                } else if (resultCode == ImagePicker.RESULT_ERROR) {
                    Toast.makeText(applicationContext, ImagePicker.getError(data), Toast.LENGTH_SHORT)
                        .show()
                }
            }
    }

    override fun onStart() {
        super.onStart()

        //hiding system bars
        uiHelper?.hide()
    }

    override fun onResume() {
        super.onResume()
        jCameraView!!.onResume()
    }

    override fun onPause() {
        super.onPause()
        jCameraView!!.onPause()
    }

}