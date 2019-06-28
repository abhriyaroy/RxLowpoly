package com.zebrostudio.rxlowpoly.helpers

import android.net.Uri
import android.os.Environment
import io.reactivex.Single
import java.io.File

interface StorageHelper {
  fun getFileToSaveImage(fileName: String): Single<File>
  fun getUriToSaveImage(fileName: String): Single<Uri>
}

class StorageHelperImpl : StorageHelper {
  override fun getFileToSaveImage(fileName: String): Single<File> {
    return Single.create {
      it.onSuccess(getFile(fileName))
    }
  }

  override fun getUriToSaveImage(fileName: String): Single<Uri> {
    return Single.create {
      it.onSuccess(Uri.fromFile(getFile(fileName)))
    }
  }

  private fun getFile(fileName: String): File {
    val directory =
      File(Environment.getExternalStorageDirectory().path + File.separator + "RxLowpolyExample")
    if (!directory.exists()) {
      directory.mkdirs()
    }
    val file = File(directory, fileName + "-" + System.currentTimeMillis() + ".png")
    if (!file.exists()) {
      file.createNewFile()
    }
    return file
  }
}