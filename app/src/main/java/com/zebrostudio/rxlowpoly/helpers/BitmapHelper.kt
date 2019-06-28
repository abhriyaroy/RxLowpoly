package com.zebrostudio.rxlowpoly.helpers

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.annotation.DrawableRes
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers

interface BitmapHelper {
  fun drawableToBitmap(context: Context, @DrawableRes drawable: Int): Bitmap
  fun drawableToBitmapSingle(context: Context, @DrawableRes drawable: Int): Single<Bitmap>
}

class BitmapHelperImpl : BitmapHelper {

  override fun drawableToBitmapSingle(
    context: Context, @DrawableRes drawable: Int
  ): Single<Bitmap> {
    return Single.just(drawableToBitmap(context, drawable))
      .subscribeOn(Schedulers.io())
  }

  override fun drawableToBitmap(context: Context, @DrawableRes drawable: Int): Bitmap {
    return BitmapFactory.decodeResource(context.resources, drawable)
  }
}