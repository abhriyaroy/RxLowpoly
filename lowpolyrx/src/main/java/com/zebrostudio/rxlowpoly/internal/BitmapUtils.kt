package com.zebrostudio.rxlowpoly.internal

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.provider.MediaStore
import androidx.annotation.DrawableRes

object BitmapUtils {

  fun getBitmapFromDrawable(
    context: Context,
    @DrawableRes drawableResId: Int
  ): Bitmap {
    return BitmapFactory.decodeResource(context.resources, drawableResId)
  }

  fun getBitmapFromFile(path: String): Bitmap {
    return BitmapFactory.decodeFile(path)
  }

  fun getBitmapFromUri(context: Context, input: Uri): Bitmap {
    return MediaStore.Images.Media.getBitmap(context.contentResolver, input)
  }

}