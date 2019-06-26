package com.zebrostudio.rxlowpoly.internal.builder

import android.Manifest.permission.READ_EXTERNAL_STORAGE
import android.Manifest.permission.WRITE_EXTERNAL_STORAGE
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat.checkSelfPermission

interface PermisisonManager {
  fun hasReadStoragePermission(context: Context): Boolean
  fun hasWriteStoragePermission(context: Context): Boolean
}

class PermisisonManagerImpl : PermisisonManager {

  override fun hasReadStoragePermission(context: Context): Boolean {
    return checkSelfPermission(
      context,
      READ_EXTERNAL_STORAGE
    ) == PackageManager.PERMISSION_GRANTED
  }

  override fun hasWriteStoragePermission(context: Context): Boolean {
    return checkSelfPermission(
      context,
      WRITE_EXTERNAL_STORAGE
    ) == PackageManager.PERMISSION_GRANTED
  }
}