package com.haksoy.soip.utlis

import android.graphics.Bitmap
import android.os.Build
import android.os.Environment
import com.haksoy.soip.MainApplication
import com.haksoy.soip.R
import com.haksoy.soip.data.chat.ChatType
import java.io.BufferedOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

object FileUtils {
    private val APP_FOLDER_NAME: String = MainApplication.instance.getString(R.string.app_name)
    fun generateFile(type: ChatType): File? {
        val file: File = File(
            mainAppFolder() + "/" + APP_FOLDER_NAME + "/" + "MEDIA" + "/" + generateNewName(
                type
            )
        )
        //create dirs if not exists
        if (!file.exists()) file.parentFile.mkdirs()
        return file
    }
    fun generateFile(name: String): File? {
        val file: File = File(
            mainAppFolder() + "/" + APP_FOLDER_NAME + "/" + "MEDIA" + "/" +name
        )
        //create dirs if not exists
        if (!file.exists()) file.parentFile.mkdirs()
        return file
    }

    //Main App Folder: /sdcard/FireApp/
    private fun mainAppFolder(): String? {
        val file: File = if (Build.VERSION.SDK_INT >= 30) {
            File(
                MainApplication.instance.getExternalFilesDir(null)
                    .toString()
            )
        } else {
            File(
                Environment.getExternalStorageDirectory()
                    .toString()
            )
        }
        //if the directory is not exists create it
        if (!file.exists()) file.mkdir()
        return file.absolutePath
    }

    private fun generateNewName(type: ChatType): String? {
        val date = Date()
        val sdf = SimpleDateFormat(
            "yyyyMMddSSSS",
            Locale.US
        ) //the Locale us is to use english numbers
        return getFileTypeString(type) + "_" + sdf.format(date) + getFileExtensionString(type)
    }

    private fun getFileTypeString(type: ChatType): String? {
        return when (type) {
            ChatType.SEND_IMAGE -> "IMG"
            ChatType.SEND_VIDEO -> "VID"
            else -> "FILE"
        }
    }

    private fun getFileExtensionString(type: ChatType): String? {
        return when (type) {
            ChatType.SEND_IMAGE -> ".jpg"
            ChatType.SEND_VIDEO -> ".mp4"
            else -> ""
        }
    }

    private const val IMAGE_COMPRESS_QUALITY = 70
    fun convertBitmapToJpeg(bmp: Bitmap, f: File) {
        try {
            val fout = FileOutputStream(f.path)
            val bos = BufferedOutputStream(fout)
            bmp.compress(Bitmap.CompressFormat.JPEG, IMAGE_COMPRESS_QUALITY, bos)
            bos.flush()
            bos.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }
}