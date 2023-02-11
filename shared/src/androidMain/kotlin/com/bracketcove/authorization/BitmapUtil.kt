package com.bracketcove.authorization

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore


object BitmapUtil {
    fun getBitmap(context: Context, fileUri: Uri?): Bitmap? {
        if (fileUri == null) return fileUri

        val bitmap = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            ImageDecoder.decodeBitmap(ImageDecoder.createSource(context.contentResolver, fileUri))
        } else {
            MediaStore.Images.Media.getBitmap(context.contentResolver, fileUri)
        }

        return bitmap
    }
}