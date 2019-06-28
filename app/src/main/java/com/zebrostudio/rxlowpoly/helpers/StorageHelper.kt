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
        println(it.path)
        println(File(it.path).exists())
        println(it.path.split(":")[1])
        Single.just(File(it.path.split(":")[1]))
      }
  }

  override fun getInputImageUriSingle(supportFragmentManager: FragmentManager): Single<Uri> {
    return Single.fromObservable(RxImagePicker.with(supportFragmentManager).requestImage(Sources.GALLERY))
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

  /* fun getPath(context: Context, uri: Uri): String? {
     val projection = arrayOf(MediaStore.Images.Media.DATA)
     val cursor = context.contentResolver.query(uri, projection, null, null, null) ?: return null
     val column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
     cursor.moveToFirst()
     val s = cursor.getString(column_index)
     cursor.close()
     return s
   }*/
}