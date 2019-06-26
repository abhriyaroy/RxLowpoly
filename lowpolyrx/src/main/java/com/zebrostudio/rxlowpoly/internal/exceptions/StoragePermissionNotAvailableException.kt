package com.zebrostudio.rxlowpoly.internal.exceptions

class StoragePermissionNotAvailableException(
  override val message: String = "Please grant storage permissions"
) : Exception()