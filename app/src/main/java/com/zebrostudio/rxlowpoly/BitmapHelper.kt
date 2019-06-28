package com.zebrostudio.rxlowpoly

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers

interface BitmapHelper {
  fun drawableToBitmap(drawable: Drawable): Bitmap
  fun drawableToBitmapSingle(drawable: Drawable): Single<Bitmap>
}

class BitmapHelperImpl : BitmapHelper {

  override fun drawableToBitmapSingle(drawable: Drawable): Single<Bitmap> {
    return Single.just(drawableToBitmap(drawable))
      .subscribeOn(Schedulers.io())
  }

  override fun drawableToBitmap(drawable: Drawable): Bitmap {
    var bitmap: Bitmap? = null

    if (drawable is BitmapDrawable) {
      if (drawable.bitmap != null) {
        return drawable.bitmap
      }
    }
    if (drawable.intrinsicWidth <= 0 || drawable.intrinsicHeight <= 0) {
      bitmap = Bitmap.createBitmap(
        1,
        1,
        Bitmap.Config.ARGB_8888
      ) // Single color bitmap will be created of 1x1 pixel
    } else {
      bitmap = Bitmap.createBitmap(
        drawable.intrinsicWidth,
        drawable.intrinsicHeight,
        Bitmap.Config.ARGB_8888
      )
    }
    val canvas = Canvas(bitmap!!)
    drawable.setBounds(0, 0, canvas.width, canvas.height)
    drawable.draw(canvas)
    return bitmap
  }
}