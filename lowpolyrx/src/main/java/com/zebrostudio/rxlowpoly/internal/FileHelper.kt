package com.zebrostudio.rxlowpoly.internal

import android.net.Uri
import java.io.File

interface FileHelper {
  fun isReadable(file: File): Boolean
  fun isReadable(uri: Uri): Boolean
  fun isWritable(file: File): Boolean
  fun isWritable(uri: Uri): Boolean
}

class FileHelperImpl : FileHelper {

  override fun isReadable(file: File): Boolean {
    if (file.canRead()) {
      return true
    }
    return false
  }

  override fun isReadable(uri: Uri): Boolean {
    return isReadable(File(uri.toString()))
  }

  override fun isWritable(file: File): Boolean {
    if (file.canWrite()) {
      return true
    }
    return false
  }

  override fun isWritable(uri: Uri): Boolean {
    return isWritable(File(uri.toString()))
  }
}