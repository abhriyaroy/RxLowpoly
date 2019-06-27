package com.zebrostudio.rxlowpoly.internal

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.provider.MediaStore
import androidx.annotation.DrawableRes
import com.zebrostudio.rxlowpoly.internal.exceptions.InvalidUriException
import java.io.File
import java.io.FileNotFoundException

interface BitmapUtils {
  fun getBitmapFromDrawable(
    context: Context,
    @DrawableRes drawableResId: Int
  ): Bitmap

  fun getBitmapFromFile(file: File): Bitmap

  fun getBitmapFromUri(context: Context, input: Uri): Bitmap
}

class BitmapUtilsImpl : BitmapUtils {

  override fun getBitmapFromDrawable(
    context: Context,
    @DrawableRes drawableResId: Int
  ): Bitmap {
    return BitmapFactory.decodeResource(context.resources, drawableResId)
  }

  override fun getBitmapFromFile(file: File): Bitmap {
    return BitmapFactory.decodeFile(file.absolutePath)
  }

  override fun getBitmapFromUri(context: Context, input: Uri): Bitmap {
    return try {
      MediaStore.Images.Media.getBitmap(context.contentResolver, input)
    } catch (fileNotFoundException: FileNotFoundException) {
      throw InvalidUriException()
    }
  }

}