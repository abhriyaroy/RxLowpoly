package com.zebrostudio.rxlowpoly.internal

import android.graphics.Bitmap
import android.net.Uri
import com.zebrostudio.rxlowpoly.internal.exceptions.InvalidFileException
import java.io.File
import java.io.FileOutputStream

interface StorageHelper {
  fun isReadable(file: File): Boolean
  fun writeBitmap(bitmap: Bitmap, file: File)
  fun writeBitmap(bitmap: Bitmap, uri: Uri)
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
  override fun writeBitmap(bitmap: Bitmap, uri: Uri) {
    if (isWritable(uri)) {
      with(File(uri.toString())) {
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

  private fun isWritable(uri: Uri): Boolean {
    return isWritable(File(uri.toString()))
  }
}