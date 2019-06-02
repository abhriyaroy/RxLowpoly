package com.zebrostudio.lowpolyrxjava

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import android.util.Log

class LowPoly private constructor() {

  init {
    throw IllegalStateException("no instance!")
  }

  companion object {
    private val TAG = "LowPoly"

    /**
     * generate lowpoly picture
     *
     * @param input             src Bitmap
     * @param alphaOrPointCount (0.0, 1) or [1, max)
     * @param fill              fill ? Paint.Style.FILL : Paint.Style.STROKE
     * @return out Bitmap
     */
    fun lowPoly(input: Bitmap, alphaOrPointCount: Float, fill: Boolean): Bitmap {
      return generate(input, 50, alphaOrPointCount, true, fill)
    }

    /**
     * generate sandpainting
     *
     * @param input             src Bitmap
     * @param threshold         recommend（30, 90）
     * @param alphaOrPointCount (0.0, 1) or [1, max)
     * @return out Bitmap
     */
    fun sandPainting(input: Bitmap, threshold: Int, alphaOrPointCount: Float): Bitmap {
      return generate(input, threshold, alphaOrPointCount, false, false)
    }

    fun generate(
      input: Bitmap,
      threshold: Int,
      alphaOrPointCount: Float,
      lowPoly: Boolean,
      fill: Boolean
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
      if (!lowPoly) {
        Log.w(TAG, "when lowPoly = false, ignore fill boolean.")
      }
      val triangles = getTriangles(pixels, width, height, threshold, alphaOrPointCount, lowPoly)
      if (lowPoly) {
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
          i = i + 6
        }
      } else {
        var i = 0
        val n = triangles.size
        while (i + 1 < n) {
          x1 = triangles[i]
          y1 = triangles[i + 1]
          val color = input.getPixel(x1, y1)
          paint.color = color
          canvas.drawCircle(x1.toFloat(), y1.toFloat(), 1f, paint)
          i += 2
        }
      }
      return newImage
    }

    init {
      System.loadLibrary("lowpoly-lib")
    }

    private external fun getTriangles(
      pixels: IntArray,
      width: Int,
      height: Int,
      threshold: Int,
      alpha_count: Float,
      lowPoly: Boolean
    ): IntArray
  }
}
