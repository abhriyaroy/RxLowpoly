package com.zebrostudio.rxlowpoly.internal.exceptions

class StoragePermissionNotAvailableException(
  override val message: String = "Storage not accessible due to missing permissions"
) : Exception()