package com.zebrostudio.rxlowpoly.internal

import android.content.Context
import android.database.Cursor
import android.graphics.Bitmap
import android.net.Uri
import android.provider.MediaStore
import com.zebrostudio.rxlowpoly.internal.exceptions.InvalidFileException
import java.io.File
import java.io.FileOutputStream

interface StorageHelper {
  fun isReadable(file: File): Boolean
  fun writeBitmap(bitmap: Bitmap, file: File)
  fun writeBitmap(context: Context, bitmap: Bitmap, uri: Uri)
}

class StorageHelperImpl : StorageHelper {

  override fun isReadable(file: File): Boolean {
    if (file.canRead()) {
      return true
    }
    return false
  }

  @Throws(InvalidFileException::class)
  override fun writeBitmap(bitmap: Bitmap, file: File) {
    if (isWritable(file)) {
      with(FileOutputStream(file)) {
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, this)
        close()
      }
    } else {
      throw InvalidFileException("File is not writable")
    }
  }

  @Throws(InvalidFileException::class)
  override fun writeBitmap(context: Context, bitmap: Bitmap, uri: Uri) {
    if (isWritable(context, uri)) {
      with(File(uri.path)) {
        writeBitmap(bitmap, this)
      }
    } else {
      throw InvalidFileException("Uri is not writable")
    }
  }

  private fun isWritable(file: File): Boolean {
    if (file.canWrite()) {
      return true
    }
    return false
  }

  private fun isWritable(context: Context, uri: Uri): Boolean {
    return isWritable(File(getPath(context, uri)))
  }

  @Throws(InvalidFileException::class)
  private fun getPath(context: Context, uri: Uri): String? {
    val file = File(uri.path)
    if (file.exists()) {
      return uri.path
    }
    var cursor: Cursor? = null
    try {
      val projection = arrayOf(MediaStore.Images.Media.DATA)
      cursor = context.contentResolver
        .query(uri, projection, null, null, null) ?: return null
      val columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
      cursor.moveToFirst()
      val s = cursor.getString(columnIndex)

      return s
    } catch (npe: NullPointerException) {
      throw InvalidFileException("Uri is not writable")
    } finally {
      cursor?.close()
    }
  }
}