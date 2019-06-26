package com.zebrostudio.rxlowpoly.internal

import com.zebrostudio.rxlowpoly.internal.exceptions.InvalidFileException
import java.io.File

interface FileHelper {
  fun isReadable(file: File): Boolean
  fun isWriteable(file: File): Boolean
}

class FileHelperImpl : FileHelper {

  @Throws(InvalidFileException::class)
  override fun isReadable(file: File): Boolean {
    if (file.canRead()) {
      return true
    } else {
      throw InvalidFileException("File is not readable")
    }
  }

  override fun isWriteable(file: File): Boolean {
    if (file.canWrite()) {
      return true
    } else {
      throw InvalidFileException("File is not writable")
    }
  }
}