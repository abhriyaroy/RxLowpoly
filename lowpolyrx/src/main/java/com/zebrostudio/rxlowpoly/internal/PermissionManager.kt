package com.zebrostudio.rxlowpoly.internal

import android.Manifest.permission.READ_EXTERNAL_STORAGE
import android.Manifest.permission.WRITE_EXTERNAL_STORAGE
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat.checkSelfPermission
import com.zebrostudio.rxlowpoly.internal.exceptions.StoragePermissionNotAvailableException

interface PermissionManager {
  fun hasReadStoragePermission(context: Context): Boolean
  fun hasWriteStoragePermission(context: Context): Boolean
}

class PermissionManagerImpl : PermissionManager {

  @Throws(StoragePermissionNotAvailableException::class)
  override fun hasReadStoragePermission(context: Context): Boolean {
    if (checkSelfPermission(
        context,
        READ_EXTERNAL_STORAGE
      ) == PackageManager.PERMISSION_GRANTED
    ) {
      return true
    } else {
      throw StoragePermissionNotAvailableException("Read permission is not available")
    }
  }

  @Throws(StoragePermissionNotAvailableException::class)
  override fun hasWriteStoragePermission(context: Context): Boolean {
    if (checkSelfPermission(
        context,
        WRITE_EXTERNAL_STORAGE
      ) == PackageManager.PERMISSION_GRANTED
    ) {
      return true
    } else {
      throw StoragePermissionNotAvailableException("Write permission is not available")
    }
  }
}