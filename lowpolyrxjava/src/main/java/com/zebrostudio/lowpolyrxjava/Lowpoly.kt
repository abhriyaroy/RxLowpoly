package com.zebrostudio.lowpolyrxjava

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import java.io.IOException
import java.util.ArrayList

object LowPoly {

  @Throws(IOException::class)
  fun generate(bitmap: Bitmap): Single<Bitmap> {
    return generate(bitmap, 50, 1f, true, false)
      .subscribeOn(Schedulers.computation())
  }

  @Throws(IOException::class)
  fun generate(
    inputBitmap: Bitmap?,
    accuracy: Int, scale: Float,
    fill: Boolean,
    antiAliasing: Boolean,
    resultWidth: Int = 960,
    resultHeight: Int = 720
  ): Single<Bitmap> {
    return Single.create {
      if (inputBitmap == null) {
        it.onError(NullPointerException("Input bitmap cannot be null"))
      }
      val image = Bitmap.createScaledBitmap(inputBitmap!!, resultWidth, resultHeight, false)

      val width = image.width
      val height = image.height

      val collectors = ArrayList<IntArray>()
      val particles = ArrayList<IntArray>()

      Sobel.sobel(image, object : Sobel.SobelCallback {
        override fun call(magnitude: Int, x: Int, y: Int) {
          if (magnitude > 40) {
            collectors.add(intArrayOf(x, y))
          }
        }
      })

      for (i in 0..99) {
        particles.add(intArrayOf((Math.random() * width).toInt(), (Math.random() * height).toInt()))
      }

      val len = collectors.size / accuracy
      for (i in 0 until len) {
        val random = (Math.random() * collectors.size).toInt()
        particles.add(collectors[random])
        collectors.removeAt(random)
      }

      particles.add(intArrayOf(0, 0))
      particles.add(intArrayOf(0, height))
      particles.add(intArrayOf(width, 0))
      particles.add(intArrayOf(width, height))

      val triangles = DelaunayTriangularize.triangulate(particles)

      var x1: Float
      var x2: Float
      var x3: Float
      var y1: Float
      var y2: Float
      var y3: Float
      var cx: Float
      var cy: Float

      val outputBitmap = Bitmap.createBitmap(
        (width * scale).toInt(), (height * scale).toInt(),
        Bitmap.Config.ARGB_8888
      )

      val canvas = Canvas(outputBitmap)
      val paint = Paint()
      paint.isAntiAlias = antiAliasing
      paint.style = if (fill) Paint.Style.FILL else Paint.Style.STROKE

      var i = 0
      while (i < triangles.size) {
        x1 = particles[triangles[i]][0].toFloat()
        x2 = particles[triangles[i + 1]][0].toFloat()
        x3 = particles[triangles[i + 2]][0].toFloat()
        y1 = particles[triangles[i]][1].toFloat()
        y2 = particles[triangles[i + 1]][1].toFloat()
        y3 = particles[triangles[i + 2]][1].toFloat()

        cx = (x1 + x2 + x3) / 3
        cy = (y1 + y2 + y3) / 3

        val path = Path()
        path.moveTo(x1, y1)
        path.lineTo(x2, y2)
        path.lineTo(x3, y3)
        path.close()

        paint.color = image.getPixel(cx.toInt(), cy.toInt())

        canvas.drawPath(path, paint)
        i += 3
      }
      it.onSuccess(outputBitmap)
    }
  }
}
