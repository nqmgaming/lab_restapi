package com.nqmgaming.lab6_minhnqph31902.utils

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.provider.MediaStore
import android.util.Log
import java.io.File
import java.io.FileOutputStream

object ImageUtils {

    fun saveBitmapToFile(context: Context, bitmap: Bitmap): Uri {
        val file = File(context.externalCacheDir, "${System.currentTimeMillis()}.jpg")
        FileOutputStream(file).use { fos ->
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos)
        }
        return Uri.fromFile(file)
    }

    fun uriToFile(context: Context, uri: Uri): File {
        Log.d("ImageUtils", "uri: $uri")
        val cursor = context.contentResolver.query(uri, null, null, null, null)
        try {
            cursor?.use {
                it.moveToFirst()
                val index = it.getColumnIndex(MediaStore.Images.ImageColumns.DATA)
                val path = it.getString(index)
                return File(path)
            }
        } catch (e: Exception) {
            Log.e("ImageUtils", "uriToFile: ${e.message}")
        }
        throw IllegalArgumentException("Uri not found")
    }
}