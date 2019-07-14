package com.zebrostudio.rxlowpoly.internal

import android.Manifest.permission.READ_EXTERNAL_STORAGE
import android.Manifest.permission.WRITE_EXTERNAL_STORAGE
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat.checkSelfPermission

interface PermissionManager {
  fun hasReadStoragePermission(context: Context): Boolean
  fun hasWriteStoragePermission(context: Context): Boolean
}

class PermissionManagerImpl : PermissionManager {

  /**
   * Returns true if external storage read permission is present.
   *
   * @param context The application context.
   * @return Boolean.
   */
  override fun hasReadStoragePermission(context: Context): Boolean {
    if (checkSelfPermission(
        context,
        READ_EXTERNAL_STORAGE
      ) == PackageManager.PERMISSION_GRANTED
    ) {
      return true
    }
    return false
  }

  /**
   * Returns true if external storage write permission is present.
   *
   * @param context The application [Context].
   * @return Boolean.
   */
  override fun hasWriteStoragePermission(context: Context): Boolean {
    if (checkSelfPermission(
        context,
        WRITE_EXTERNAL_STORAGE
      ) == PackageManager.PERMISSION_GRANTED
    ) {
      return true
    }
    return false
  }
}