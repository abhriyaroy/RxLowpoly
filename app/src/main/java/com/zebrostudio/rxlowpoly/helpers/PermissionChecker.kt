package com.zebrostudio.rxlowpoly.helpers

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat.checkSelfPermission

interface PermissionChecker {
  fun isStoragePermissionAvailable(context: Context): Boolean
}

class PermissionCheckerImpl : PermissionChecker {

  override fun isStoragePermissionAvailable(context: Context): Boolean {
    return checkSelfPermission(
      context,
      Manifest.permission.READ_EXTERNAL_STORAGE
    ) == PackageManager.PERMISSION_GRANTED && checkSelfPermission(
      context,
      Manifest.permission.WRITE_EXTERNAL_STORAGE
    ) == PackageManager.PERMISSION_GRANTED
  }
}