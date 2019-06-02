package com.zebrostudio.lowpolyrxjava

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import android.support.annotation.DrawableRes
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers

private const val LOWPOLY_RX_SO_FILENAME = "lowpolyrx-lib"
private const val POINT_COUNT = 1000F

class LowPolyRx {

  fun getLowPolyImage(
    context: Context,
    @DrawableRes drawableResId: Int,
    pointCount: Float = POINT_COUNT
  ): Single<Bitmap> {
    return getBitmapFromDrawable(context, drawableResId)
        .flatMap {
          getLowPolyImage(it, pointCount)
        }
        .subscribeOn(Schedulers.io())
  }

  fun getLowPolyImage(
    filePath: String,
    pointCount: Float = POINT_COUNT
  ): Single<Bitmap> {
    return getBitmapFromFile(filePath)
        .flatMap {
          getLowPolyImage(it, pointCount)
        }
        .subscribeOn(Schedulers.io())
  }

  fun getLowPolyImage(
    inputBitmap: Bitmap,
    pointCount: Float = POINT_COUNT
  ): Single<Bitmap> {
    return generateLowpolyFromScaledDownBitmap(inputBitmap, pointCount)
        .subscribeOn(Schedulers.io())
  }

  private fun getBitmapFromDrawable(context: Context, @DrawableRes drawableResId: Int): Single<Bitmap> {
    return Single.create {
      it.onSuccess(BitmapFactory.decodeResource(context.resources, drawableResId))
    }
  }

  private fun getBitmapFromFile(path: String): Single<Bitmap> {
    return Single.create { emitter ->
      emitter.onSuccess(BitmapFactory.decodeFile(path))
    }
  }

  private fun generateLowpolyFromScaledDownBitmap(
    inputBitmap: Bitmap,
    pointCount: Float
  ): Single<Bitmap> {
    return Single.create { emitter ->
      Bitmap.createScaledBitmap(
          inputBitmap, (inputBitmap.width * 0.3).toInt(),
          (inputBitmap.height * 0.3).toInt(), false
      )
          .run {
            generate(this, pointCount, true).let {
              emitter.onSuccess(it)
            }
          }
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

  companion object {

    init {
      System.loadLibrary(LOWPOLY_RX_SO_FILENAME)
    }

  }

}
