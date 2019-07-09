package com.zebrostudio.rxlowpoly.internal

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.ParcelFileDescriptor
import androidx.annotation.DrawableRes
import com.zebrostudio.rxlowpoly.internal.exceptions.InvalidUriException
import java.io.File
import java.io.FileDescriptor
import java.io.FileNotFoundException

interface BitmapUtils {
  fun getBitmapFromDrawable(
    context: Context,
    @DrawableRes drawableResId: Int
  ): Bitmap

  fun getBitmapFromFile(file: File): Bitmap

  fun getBitmapFromUri(context: Context, uri: Uri): Bitmap
}

class BitmapUtilsImpl : BitmapUtils {

  /**
   * Returns the [Bitmap] from a [DrawableRes].
   *
   * @param context The application [Context].
   * @param drawableResId The id of the drawable resource.
   * @return Bitmap.
   */
  override fun getBitmapFromDrawable(
    context: Context,
    @DrawableRes drawableResId: Int
  ): Bitmap {
    return BitmapFactory.decodeResource(context.resources, drawableResId)
  }

  /**
   * Returns the [Bitmap] from a specified [File].
   *
   * @param file The input [File].
   * @return Bitmap.
   */
  override fun getBitmapFromFile(file: File): Bitmap {
    return BitmapFactory.decodeFile(file.absolutePath)
  }

  /**
   * Returns the [Bitmap] from a specified [Uri].
   *
   * @param context The application [Context].
   * @param  uri The input uri.
   * @return Bitmap.
   */
  override fun getBitmapFromUri(context: Context, uri: Uri): Bitmap {
    return try {
      val parcelFileDescriptor: ParcelFileDescriptor =
        context.contentResolver.openFileDescriptor(uri, "r")
      val fileDescriptor: FileDescriptor = parcelFileDescriptor.fileDescriptor
      val image: Bitmap = BitmapFactory.decodeFileDescriptor(fileDescriptor)
      parcelFileDescriptor.close()
      image
    } catch (fileNotFoundException: FileNotFoundException) {
      throw InvalidUriException()
    }
  }
}