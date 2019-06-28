package com.zebrostudio.rxlowpoly.helpers

import android.net.Uri
import android.os.Environment
import androidx.fragment.app.FragmentManager
import com.mlsdev.rximagepicker.RxImagePicker
import com.mlsdev.rximagepicker.Sources
import io.reactivex.Single
import java.io.File

interface StorageHelper {
  fun getFileToSaveImage(fileName: String): Single<File>
  fun getUriToSaveImage(fileName: String): Single<Uri>
  fun getInputImageFileSingle(supportFragmentManager: FragmentManager): Single<File>
  fun getInputImageUriSingle(supportFragmentManager: FragmentManager): Single<Uri>
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

  override fun getInputImageFileSingle(supportFragmentManager: FragmentManager): Single<File> {
    return Single.fromObservable(RxImagePicker.with(supportFragmentManager).requestImage(Sources.GALLERY))
      .flatMap {
        if (it == null){
          throw Exception("No file is chosen")
        }
        Single.just(File(it.path))
      }
  }

  override fun getInputImageUriSingle(supportFragmentManager: FragmentManager): Single<Uri> {
    return Single.fromObservable(RxImagePicker.with(supportFragmentManager).requestImage(Sources.GALLERY))
      .map {
        if (it == null){
          throw Exception("No file is chosen")
        }
        it
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