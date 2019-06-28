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
      val file =
        File(Environment.DIRECTORY_PICTURES + File.separator + fileName + "-" + System.currentTimeMillis() + ".png")
      file.createNewFile()
      it.onSuccess(file)
    }
  }

  override fun getUriToSaveImage(fileName: String): Single<Uri> {
    return Single.create {
      val file =
        File(Environment.DIRECTORY_PICTURES + File.separator + fileName + "-" + System.currentTimeMillis() + ".png")
      file.createNewFile()
      it.onSuccess(Uri.fromFile(file))
    }
  }
}