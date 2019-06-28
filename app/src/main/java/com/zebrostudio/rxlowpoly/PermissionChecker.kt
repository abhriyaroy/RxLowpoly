package com.zebrostudio.rxlowpoly

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
      Manifest.permission_group.STORAGE
    ) == PackageManager.PERMISSION_GRANTED
  }
}