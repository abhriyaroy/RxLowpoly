package com.zebrostudio.lowpolyrx

import android.content.Context
import android.graphics.*
import android.net.Uri
import android.provider.MediaStore
import androidx.annotation.DrawableRes
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers

private const val LOWPOLY_RX_SO_FILENAME = "lowpolyrx-lib"
private const val POINT_COUNT = 8000F
private const val THRESHOLD = 1080

class LowPolyRx private constructor() {

  fun lowPolyImageSingle(
    context: Context,
    uri: Uri,
    pointCount: Float
  ): Single<Bitmap> {
    return getBitmapFromUri(context, uri)
        .flatMap {
          lowPolyImageSingle(it, pointCount)
        }
        .subscribeOn(Schedulers.io())
  }

  fun lowPolyImageSingle(
    context: Context,
    @DrawableRes drawableResId: Int,
    pointCount: Float
  ): Single<Bitmap> {
    return getBitmapFromDrawable(context, drawableResId)
        .flatMap {
          lowPolyImageSingle(it, pointCount)
        }
        .subscribeOn(Schedulers.io())
  }

  fun lowPolyImageSingle(
    filePath: String,
    pointCount: Float
  ): Single<Bitmap> {
    return getBitmapFromFile(filePath)
        .flatMap {
          lowPolyImageSingle(it, pointCount)
        }
        .subscribeOn(Schedulers.io())
  }

  fun lowPolyImageSingle(
    inputBitmap: Bitmap,
    pointCount: Float
  ): Single<Bitmap> {
    return Single.just(generate(inputBitmap, pointCount, true))
        .subscribeOn(Schedulers.io())
  }

  private fun getBitmapFromUri(context: Context, input: Uri): Single<Bitmap> {
    return Single.create { emitter ->
      emitter.onSuccess(MediaStore.Images.Media.getBitmap(context.contentResolver, input).let {
        getScaledDownBitmap(it, THRESHOLD)
      })
    }
  }

  private fun getBitmapFromDrawable(context: Context,
    @DrawableRes drawableResId: Int): Single<Bitmap> {
    return Single.create { emitter ->
      emitter.onSuccess(BitmapFactory.decodeResource(context.resources, drawableResId).let {
        getScaledDownBitmap(it, THRESHOLD)
      })
    }
  }

  private fun getBitmapFromFile(path: String): Single<Bitmap> {
    return Single.create { emitter ->
      emitter.onSuccess(BitmapFactory.decodeFile(path).let {
        getScaledDownBitmap(it, THRESHOLD)
      })
    }
  }

  private fun generate(
    input: Bitmap,
    pointCount: Float,
    fill: Boolean,
    threshold: Int = 50
  ): Bitmap {
    val width = input.width
    val height = input.height

    val newImage = Bitmap.createBitmap(input.width, input.height, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(newImage)
    val paint = Paint()
    paint.isAntiAlias = false
    paint.style = if (fill) Paint.Style.FILL else Paint.Style.STROKE
    var x1: Int
    var x2: Int
    var x3: Int
    var y1: Int
    var y2: Int
    var y3: Int
    val pixels = IntArray(width * height)
    input.getPixels(pixels, 0, width, 0, 0, width, height)
    val triangles = getTriangles(pixels, width, height, threshold, pointCount)
    val path = Path()
    var i = 0
    while (i + 5 < triangles.size) {
      x1 = triangles[i]
      y1 = triangles[i + 1]
      x2 = triangles[i + 2]
      y2 = triangles[i + 3]
      x3 = triangles[i + 4]
      y3 = triangles[i + 5]

      val color = input.getPixel((x1 + x2 + x3) / 3, (y1 + y2 + y3) / 3)
      path.rewind()
      path.moveTo(x1.toFloat(), y1.toFloat())
      path.lineTo(x2.toFloat(), y2.toFloat())
      path.lineTo(x3.toFloat(), y3.toFloat())
      path.close()
      paint.color = color
      canvas.drawPath(path, paint)
      i += 6
    }
    return newImage
  }

  private external fun getTriangles(
    pixels: IntArray,
    width: Int,
    height: Int,
    threshold: Int,
    point_count: Float
  ): IntArray

  private fun getScaledDownBitmap(bitmap: Bitmap, threshold: Int): Bitmap {
    val width = bitmap.width
    val height = bitmap.height
    var newWidth = width
    var newHeight = height

    if (width > height && width > threshold) {
      newWidth = threshold
      newHeight = (height * newWidth.toFloat() / width).toInt()
    }

    if (width > height && width <= threshold) {
      return bitmap
    }

    if (width < height && height > threshold) {
      newHeight = threshold
      newWidth = (width * newHeight.toFloat() / height).toInt()
    }

    if (width < height && height <= threshold) {
      return bitmap
    }

    if (width == height && width > threshold) {
      newWidth = threshold
      newHeight = newWidth
    }

    return if (width == height && width <= threshold) {
      bitmap
    } else {
      getResizedBitmap(bitmap, newWidth, newHeight)
    }

  }

  private fun getResizedBitmap(bm: Bitmap,
    newWidth: Int,
    newHeight: Int): Bitmap {
    val width = bm.width
    val height = bm.height
    val scaleWidth = newWidth.toFloat() / width
    val scaleHeight = newHeight.toFloat() / height
    val matrix = Matrix()
    matrix.postScale(scaleWidth, scaleHeight)
    return Bitmap.createBitmap(bm, 0, 0, width, height, matrix, false)
  }

  companion object {

    init {
      System.loadLibrary(LOWPOLY_RX_SO_FILENAME)
    }

    @JvmOverloads
    fun generateLowpoly(context: Context,
      uri: Uri,
      pointCount: Float = POINT_COUNT): Single<Bitmap> {
      return LowPolyRx().lowPolyImageSingle(context, uri, pointCount)
    }

    @JvmOverloads
    fun generateLowpoly(context: Context,
      @DrawableRes drawableResId: Int,
      pointCount: Float = POINT_COUNT): Single<Bitmap> {
      return LowPolyRx().lowPolyImageSingle(context, drawableResId, pointCount)
    }

    @JvmOverloads
    fun generateLowpoly(filePath: String, pointCount: Float = POINT_COUNT): Single<Bitmap> {
      return LowPolyRx().lowPolyImageSingle(filePath, pointCount)
    }

    @JvmOverloads
    fun generateLowpoly(inputBitmap: Bitmap, pointCount: Float = POINT_COUNT): Single<Bitmap> {
      return LowPolyRx().lowPolyImageSingle(inputBitmap, pointCount)
    }

  }

}
