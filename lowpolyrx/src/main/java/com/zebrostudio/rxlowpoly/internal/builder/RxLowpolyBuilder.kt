package com.zebrostudio.rxlowpoly.internal.builder

import android.content.Context
import android.graphics.*
import android.net.Uri
import androidx.annotation.CheckResult
import androidx.annotation.DrawableRes
import com.zebrostudio.rxlowpoly.Quality
import com.zebrostudio.rxlowpoly.internal.*
import com.zebrostudio.rxlowpoly.internal.builder.InputType.*
import com.zebrostudio.rxlowpoly.internal.exceptions.InvalidFileException
import com.zebrostudio.rxlowpoly.internal.exceptions.StoragePermissionNotAvailableException
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import java.io.File

private const val LOWPOLY_RX_SO_FILENAME = "lowpolyrx-lib"

class RxLowpolyBuilder {
  private var permissionsManager: PermissionManager = PermissionManagerImpl()
  private var storageHelper: StorageHelper = StorageHelperImpl()
  private var bitmapUtils: BitmapUtils = BitmapUtilsImpl()
  private lateinit var context: Context
  private lateinit var inputType: InputType
  private lateinit var inputBitmap: Bitmap
  private lateinit var inputFile: File
  private lateinit var inputUri: Uri
  private lateinit var outputFile: File
  private lateinit var outputUri: Uri
  private var inputDrawableId: Int = 0
  private var quality: Quality = Quality.VERY_HIGH
  private var maxWidth: Int = 1024
  private var downScalingFactor = 1f
  private var shouldSaveOutputToFile = false
  private var shouldSaveOutputToUri = false

  init {
    System.loadLibrary(LOWPOLY_RX_SO_FILENAME)
  }

  fun init(context: Context): RxLowpolyBuilder {
    this.context = context
    return this
  }

  @CheckResult
  fun quaity(quality: Quality): RxLowpolyBuilder {
    this.quality = quality
    return this
  }

  @CheckResult
  fun overrideScaling(downScalingFactor: Float): RxLowpolyBuilder {
    this.downScalingFactor = Math.abs(downScalingFactor)
    return this
  }

  @CheckResult
  fun overrideScaling(maxWidth: Int): RxLowpolyBuilder {
    this.maxWidth = Math.abs(maxWidth)
    return this
  }

  @CheckResult
  fun overrideScaling(downScalingFactor: Float, maxWidth: Int): RxLowpolyBuilder {
    this.downScalingFactor = Math.abs(downScalingFactor)
    this.maxWidth = Math.abs(maxWidth)
    return this
  }

  @CheckResult
  fun input(bitmap: Bitmap): RxLowpolyBuilder {
    inputType = BITMAP
    inputBitmap = bitmap
    return this
  }

  @CheckResult
  fun input(@DrawableRes drawableIdRes: Int): RxLowpolyBuilder {
    inputType = DRAWABLE
    inputDrawableId = drawableIdRes
    return this
  }

  @CheckResult
  fun input(file: File): RxLowpolyBuilder {
    inputType = FILE
    inputFile = file
    return this
  }

  @CheckResult
  fun input(uri: Uri): RxLowpolyBuilder {
    inputType = URI
    inputUri = uri
    return this
  }

  @CheckResult
  fun output(file: File): RxLowpolyBuilder {
    shouldSaveOutputToFile = true
    outputFile = file
    return this
  }

  @CheckResult
  fun output(uri: Uri): RxLowpolyBuilder {
    shouldSaveOutputToUri = true
    outputUri = uri
    return this
  }

  @CheckResult
  fun generateAsyn(): Single<Bitmap> {
    return getBitmap()
      .subscribeOn(Schedulers.io())
  }

  @Throws(StoragePermissionNotAvailableException::class, InvalidFileException::class)
  fun generate(): Bitmap {
    with(getInputBitmap()) {
      with(getScaledDownBitmap(this, downScalingFactor, maxWidth)) {
        with(generate(this, quality.pointCount, true)) {
          if (shouldSaveOutputToFile) {
            saveBitmapToFile(this)
          } else if (shouldSaveOutputToUri) {
            saveBitmapToUri(this)
          }
          return this
        }
      }
    }
  }

  private fun getBitmap(): Single<Bitmap> {
    return Single.create { emitter ->
      with(generate()) {
        emitter.onSuccess(this)
      }
    }
  }

  private fun getInputBitmap(): Bitmap {
    return when (inputType) {
      BITMAP -> inputBitmap
      DRAWABLE -> bitmapUtils.getBitmapFromDrawable(context, inputDrawableId)
      FILE -> {
        if (permissionsManager.hasReadStoragePermission(context)) {
          if (storageHelper.isReadable(inputFile)) {
            bitmapUtils.getBitmapFromFile(inputFile)
          } else {
            throw InvalidFileException("File is not readable")
          }
        } else {
          throw StoragePermissionNotAvailableException("Read permission is not available")
        }
      }
      URI -> {
        if (permissionsManager.hasReadStoragePermission(context)) {
          bitmapUtils.getBitmapFromUri(context, inputUri)
        } else {
          throw StoragePermissionNotAvailableException("Read permission is not available")
        }
      }
    }
  }

  private fun saveBitmapToFile(bitmap: Bitmap) {
    if (permissionsManager.hasWriteStoragePermission(context)) {
      storageHelper.writeBitmap(bitmap, outputFile)
    } else {
      throw StoragePermissionNotAvailableException("Write permission is not available")
    }
  }

  private fun saveBitmapToUri(bitmap: Bitmap) {
    if (permissionsManager.hasWriteStoragePermission(context)) {
      storageHelper.writeBitmap(bitmap, outputUri)
    } else {
      throw StoragePermissionNotAvailableException("Write permission is not available")
    }
  }

  private fun getScaledDownBitmap(bitmap: Bitmap, downScalingFactor: Float, maxWidth: Int): Bitmap {
    val downScaledWidth = bitmap.width / downScalingFactor
    val downScaledHeight = bitmap.height / downScalingFactor
    var newWidth = downScaledWidth
    var newHeight = downScaledHeight

    if ((downScalingFactor == 1f && downScaledWidth > downScaledHeight && downScaledWidth <= maxWidth)
      || (downScalingFactor == 1f && downScaledHeight > downScaledWidth && downScaledHeight <= maxWidth)
    ) {
      return bitmap
    }

    if (downScaledWidth > downScaledHeight && downScaledWidth > maxWidth) {
      newWidth = maxWidth.toFloat()
      newHeight = (downScaledHeight * newWidth / downScaledWidth)
    }

    if (downScaledWidth < downScaledHeight && downScaledHeight > maxWidth) {
      newHeight = maxWidth.toFloat()
      newWidth = (downScaledWidth * newHeight / downScaledHeight)
    }

    if (downScaledHeight == downScaledWidth && downScaledWidth > maxWidth) {
      newWidth = maxWidth.toFloat()
      newHeight = newWidth
    }

    return getResizedBitmap(bitmap, newWidth, newHeight)

  }

  private fun getResizedBitmap(
    bm: Bitmap,
    newWidth: Float,
    newHeight: Float
  ): Bitmap {
    val width = bm.width
    val height = bm.height
    val scaleWidth = newWidth / width
    val scaleHeight = newHeight / height
    val matrix = Matrix()
    matrix.postScale(scaleWidth, scaleHeight)
    return Bitmap.createBitmap(bm, 0, 0, width, height, matrix, false)
  }

  private fun generate(
    input: Bitmap,
    pointCount: Float,
    fill: Boolean
  ): Bitmap {
    val newImage = Bitmap.createBitmap(input.width, input.height, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(newImage)
    val paint = Paint()
    paint.isAntiAlias = false
    paint.style = if (fill) Paint.Style.FILL else Paint.Style.STROKE
    drawTriangles(input.width, input.height, input, pointCount, paint, canvas)
    return newImage
  }

  private fun drawTriangles(
    width: Int,
    height: Int,
    input: Bitmap,
    pointCount: Float,
    paint: Paint,
    canvas: Canvas
  ) {
    val pixels = IntArray(width * height)
    input.getPixels(pixels, 0, width, 0, 0, width, height)
    val triangles = getTriangles(pixels, width, height, pointCount)
    drawTrianglePath(paint, canvas, triangles, input)
  }

  private fun drawTrianglePath(paint: Paint, canvas: Canvas, triangles: IntArray, input: Bitmap) {
    val path = Path()
    var i = 0
    var x1: Int
    var x2: Int
    var x3: Int
    var y1: Int
    var y2: Int
    var y3: Int
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
  }

  private external fun getTriangles(
    pixels: IntArray,
    width: Int,
    height: Int,
    point_count: Float
  ): IntArray

}