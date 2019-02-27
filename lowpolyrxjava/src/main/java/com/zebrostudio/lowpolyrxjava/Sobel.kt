package com.zebrostudio.lowpolyrxjava

import android.graphics.Bitmap

object Sobel {

  fun sobel(image: Bitmap, callback: SobelCallback) {
    val kernelX = arrayOf(intArrayOf(-1, 0, 1), intArrayOf(-2, 0, 2), intArrayOf(-1, 0, 1))
    val kernelY = arrayOf(intArrayOf(-1, -2, -1), intArrayOf(0, 0, 0), intArrayOf(1, 2, 1))
    val width = image.width
    val height = image.height

    for (y in 0 until height) {
      for (x in 0 until width) {
        val pixelX = kernelX[0][0] * getAverage(image, x - 1, y - 1) +
            kernelX[0][1] * getAverage(image, x, y - 1) +
            kernelX[0][2] * getAverage(image, x + 1, y - 1) +
            kernelX[1][0] * getAverage(image, x - 1, y) +
            kernelX[1][1] * getAverage(image, x, y) +
            kernelX[1][2] * getAverage(image, x + 1, y) +
            kernelX[2][0] * getAverage(image, x - 1, y + 1) +
            kernelX[2][1] * getAverage(image, x, y + 1) +
            kernelX[2][2] * getAverage(image, x + 1, y + 1)
        val pixelY = kernelY[0][0] * getAverage(image, x - 1, y - 1) +
            kernelY[0][1] * getAverage(image, x, y - 1) +
            kernelY[0][2] * getAverage(image, x + 1, y - 1) +
            kernelY[1][0] * getAverage(image, x - 1, y) +
            kernelY[1][1] * getAverage(image, x, y) +
            kernelY[1][2] * getAverage(image, x + 1, y) +
            kernelY[2][0] * getAverage(image, x - 1, y + 1) +
            kernelY[2][1] * getAverage(image, x, y + 1) +
            kernelY[2][2] * getAverage(image, x + 1, y + 1)

        val magnitude = Math.sqrt((pixelX * pixelX + pixelY * pixelY).toDouble()).toInt()
        callback.call(magnitude, x, y)
      }
    }
  }

  private fun getAverage(image: Bitmap, x: Int, y: Int): Int {
    if (x < 0 || y < 0 || x >= image.width || y >= image.height) {
      return 0
    }
    val color = image.getPixel(x, y)
    val blue = 0xFF and color
    val green = color shr 8 and 0xFF
    val red = color shr 16 and 0xFF
    return (blue + green + red) / 3
  }

  interface SobelCallback {
    fun call(magnitude: Int, x: Int, y: Int)
  }
}
