package com.zebrostudio.rxlowpoly.internal.exceptions

class StoragePermissionNotAvailableException : Exception(){
  override val message: String = "Please grant storage permissions"
}