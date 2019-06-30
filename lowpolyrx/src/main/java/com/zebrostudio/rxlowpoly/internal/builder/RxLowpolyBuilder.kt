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
private const val READ_PERMISSION_NOT_AVAILABLE_ERROR_MESSAGE = "Read permission is not available"
private const val WRITE_PERMISSION_NOT_AVAILABLE_ERROR_MESSAGE = "Write permission is not available"
private const val FILE_NOT_READABLE_ERROR_MESSAGE = "File is not readable"

class RxLowpolyBuilder {
  private var permissionsManager: PermissionManager = PermissionManagerImpl()
  private var storageHelper: StorageHelper = StorageHelperImpl()
  private var bitmapUtils: BitmapUtils = BitmapUtilsImpl()
  private lateinit var context: Context
  private lateinit var inputType: InputType
  private lateinit var inputBitmap: Bitmap
  private lateinit var inputFile: File
  private lateinit var inputUri: Uri
  private lateinit var outputType: OutputType
  private lateinit var outputFile: File
  private lateinit var outputUri: Uri
  private var inputDrawableId: Int = 0
  private var quality: Quality = Quality.HIGH
  private var maxWidth: Int = 1024
  private var downScalingFactor = 1.0f
  private var shouldSaveOutput = false

  /**
   * Loads the native so file.
   */
  init {
    System.loadLibrary(LOWPOLY_RX_SO_FILENAME)
  }

  /**
   * Begins a lowpoly generation by passing in the application [Context].
   * This method is called internally.
   *
   * @param context The application [Context].
   * @return This instance of the [RxLowpolyBuilder].
   */
  internal fun init(context: Context): RxLowpolyBuilder {
    this.context = context
    return this
  }

  /**
   * Replaces the default [Quality.HIGH] with the supplied [quality].
   * This method call is optional.
   *
   * @param quality The application [Context].
   * @return This instance of the [RxLowpolyBuilder].
   */
  @CheckResult
  fun quality(quality: Quality): RxLowpolyBuilder {
    this.quality = quality
    return this
  }

  /**
   * Replaces the default [downScalingFactor] of 1.0f with the supplied downscaling factor.
   * This method call is optional.
   *
   * @param  downScalingFactor The float factor by which the image should be downscaled.
   * @return This instance of the [RxLowpolyBuilder].
   */
  @CheckResult
  fun overrideScaling(
    downScalingFactor: Float
  ): RxLowpolyBuilder {
    this.downScalingFactor = Math.abs(downScalingFactor)
    return this
  }

  /**
   * Replaces the default [maxWidth] of 1024 with the supplied maximum width.
   * This method call is optional.
   *
   * @param  maxWidth The maximum width of the image in integer.
   * @return This instance of the [RxLowpolyBuilder].
   */
  @CheckResult
  fun overrideScaling(
    maxWidth: Int
  ): RxLowpolyBuilder {
    this.maxWidth = Math.abs(maxWidth)
    return this
  }

  /**
   * Replaces the default [downScalingFactor] of 1.0f with the supplied [downScalingFactor] and also
   * replaces the default [maxWidth] of 1024 with the supplied maximum width.
   * This method call is optional.
   *
   * @param  downScalingFactor The float factor by which the image should be downscaled.
   * @param  maxWidth The maximum width of the image in integer.
   * @return This instance of the [RxLowpolyBuilder].
   */
  @CheckResult
  fun overrideScaling(
    downScalingFactor: Float = this.downScalingFactor,
    maxWidth: Int = this.maxWidth
  ): RxLowpolyBuilder {
    this.downScalingFactor = Math.abs(downScalingFactor)
    this.maxWidth = Math.abs(maxWidth)
    return this
  }

  /**
   * Takes the image [Bitmap] as input and sets the [inputType] as [InputType.BITMAP].
   * This method call is mandatory if a [Bitmap] source is the input.
   *
   * @param  bitmap The bitmap of the original image.
   * @return This instance of the [RxLowpolyBuilder].
   */
  @CheckResult
  fun input(bitmap: Bitmap): RxLowpolyBuilder {
    inputType = BITMAP
    inputBitmap = bitmap
    return this
  }

  /**
   * Takes the input [DrawableRes] and sets the [inputType] as [InputType.DRAWABLE].
   * This method call is mandatory if a [DrawableRes] is the input.
   *
   * @param  drawableIdRes The drawable resource id of the original image.
   * @return This instance of the [RxLowpolyBuilder].
   */
  @CheckResult
  fun input(@DrawableRes drawableIdRes: Int): RxLowpolyBuilder {
    inputType = DRAWABLE
    inputDrawableId = drawableIdRes
    return this
  }

  /**
   * Takes the image [File] as input and sets the [inputType] as [InputType.FILE].
   * This method call is mandatory if an image [File] is the input.
   *
   * @param  file The file containing the original image.
   * @return This instance of the [RxLowpolyBuilder].
   */
  @CheckResult
  fun input(file: File): RxLowpolyBuilder {
    inputType = FILE
    inputFile = file
    return this
  }

  /**
   * Takes the image [Uri] as input and sets the [inputType] as [InputType.URI].
   * This method call is mandatory if an image uri is the input.
   *
   * @param  uri The uri of the original image.
   * @return This instance of the [RxLowpolyBuilder].
   */
  @CheckResult
  fun input(uri: Uri): RxLowpolyBuilder {
    inputType = URI
    inputUri = uri
    return this
  }

  /**
   * Sets the output destination as a [File] and sets the [shouldSaveOutput] flag as true.
   * This method is optional and can be used to save the lowpoly image to a [File].
   *
   * @param  file The destination [File] where the lowpoly image will be saved.
   * @return This instance of the [RxLowpolyBuilder].
   */
  @CheckResult
  fun output(file: File): RxLowpolyBuilder {
    shouldSaveOutput = true
    outputType = OutputType.FILE
    outputFile = file
    return this
  }

  /**
   * Sets the output destination as an [Uri] and sets the [shouldSaveOutput] flag as true.
   * This method is optional and can be used to save the lowpoly image to an [Uri].
   *
   * @param  uri The destination [Uri] where the lowpoly image will be saved.
   * @return This instance of the [RxLowpolyBuilder].
   */
  @CheckResult
  fun output(uri: Uri): RxLowpolyBuilder {
    shouldSaveOutput = true
    outputType = OutputType.URI
    outputUri = uri
    return this
  }

  /**
   * This is a terminal method which returns a [Single] containing the bitmap of the
   * generated lowpoly image.
   *
   * This method call is mandatory if [generate] is not called.
   *
   * @return The lowpoly bitmap wrapped in a [Single].
   */
  @CheckResult
  fun generateAsync(): Single<Bitmap> {
    return getBitmap()
      .subscribeOn(Schedulers.io())
  }

  /**
   * This is a terminal method which returns a [Bitmap] containing the bitmap of the generated
   * lowpoly image.
   * This method call is mandatory if [generateAsync] is not called.
   *
   *
   * @return The lowpoly bitmap.
   * @throws StoragePermissionNotAvailableException when storage permissions are not available.
   * @throws InvalidFileException when the input or output [File] or [Uri] is invalid.
   */
  @Throws(StoragePermissionNotAvailableException::class, InvalidFileException::class)
  fun generate(): Bitmap {
    with(getInputBitmap()) {
      with(getScaledDownBitmap(this)) {
        with(generate(this, quality.pointCount)) {
          if (shouldSaveOutput) {
            when (outputType) {
              OutputType.FILE -> saveBitmapToFile(this)
              OutputType.URI -> saveBitmapToUri(this)
            }
          }
          return this
        }
      }
    }
  }

  /**
   * Makes an internal call to [generate] wrapped in a [Single].
   * This method call is mandatory if [generateAsync] is not called.
   *
   * @return The lowpoly bitmap.
   */
  private fun getBitmap(): Single<Bitmap> {
    return Single.create { emitter ->
      with(generate()) {
        emitter.onSuccess(this)
      }
    }
  }

  /**
   * Obtains the [Bitmap] from the supplied input source using [inputType].
   *
   * @return The input image [Bitmap].
   * @throws StoragePermissionNotAvailableException when storage permissions are not available.
   * @throws InvalidFileException when the input [File] is invalid.
   */
  @Throws(StoragePermissionNotAvailableException::class, InvalidFileException::class)
  private fun getInputBitmap(): Bitmap {
    return when (inputType) {
      BITMAP -> inputBitmap
      DRAWABLE -> bitmapUtils.getBitmapFromDrawable(context, inputDrawableId)
      FILE -> {
        if (permissionsManager.hasReadStoragePermission(context)) {
          if (storageHelper.isReadable(inputFile)) {
            bitmapUtils.getBitmapFromFile(inputFile)
          } else {
            throw InvalidFileException(FILE_NOT_READABLE_ERROR_MESSAGE)
          }
        } else {
          throw StoragePermissionNotAvailableException(READ_PERMISSION_NOT_AVAILABLE_ERROR_MESSAGE)
        }
      }
      URI -> {
        if (permissionsManager.hasReadStoragePermission(context)) {
          bitmapUtils.getBitmapFromUri(context, inputUri)
        } else {
          throw StoragePermissionNotAvailableException(READ_PERMISSION_NOT_AVAILABLE_ERROR_MESSAGE)
        }
      }
    }
  }

  /**
   * Writes the lowpoly [Bitmap] into the [outputFile].
   *
   * @param bitmap The input image bitmap
   * @throws StoragePermissionNotAvailableException when storage permissions are not available.
   * @throws InvalidFileException when the input [File] is invalid.
   */
  @Throws(StoragePermissionNotAvailableException::class, InvalidFileException::class)
  private fun saveBitmapToFile(bitmap: Bitmap) {
    if (permissionsManager.hasWriteStoragePermission(context)) {
      storageHelper.writeBitmap(bitmap, outputFile)
    } else {
      throw StoragePermissionNotAvailableException(WRITE_PERMISSION_NOT_AVAILABLE_ERROR_MESSAGE)
    }
  }

  /**
   * Writes the lowpoly [Bitmap] into the [outputUri].
   *
   * @param bitmap The input image [Bitmap].
   * @throws StoragePermissionNotAvailableException when storage permissions are not available.
   * @throws InvalidFileException when the input uri cannot be converted into a writable [File].
   */
  @Throws(StoragePermissionNotAvailableException::class, InvalidFileException::class)
  private fun saveBitmapToUri(bitmap: Bitmap) {
    if (permissionsManager.hasWriteStoragePermission(context)) {
      storageHelper.writeBitmap(context, bitmap, outputUri)
    } else {
      throw StoragePermissionNotAvailableException(WRITE_PERMISSION_NOT_AVAILABLE_ERROR_MESSAGE)
    }
  }

  /**
   * Calculates the width and height of the resultant scaled down [Bitmap] with respect
   * to the [downScalingFactor] and [maxWidth] while maintaining the original aspect ratio.
   * Makes a call to [getResizedBitmap] to obtain the downscaled [Bitmap].
   *
   * @param bitmap The input image [Bitmap].
   * @return The downscaled [Bitmap].
   */
  private fun getScaledDownBitmap(bitmap: Bitmap): Bitmap {
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

  /**
   * Scales down the [Bitmap] with respect to the [downScalingFactor] and [maxWidth] while
   * maintaining the original aspect ratio.
   *
   * @param bitmap The input image [Bitmap].
   * @param newWidth The width of the resultant scaled down image.
   * @param newHeight The height of the resultant scaled down image.
   * @return The downscaled [Bitmap]
   */
  private fun getResizedBitmap(
    bitmap: Bitmap,
    newWidth: Float,
    newHeight: Float
  ): Bitmap {
    val width = bitmap.width
    val height = bitmap.height
    val scaleWidth = newWidth / width
    val scaleHeight = newHeight / height
    val matrix = Matrix()
    matrix.postScale(scaleWidth, scaleHeight)
    return Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, false)
  }

  /**
   * Generates the lowpoly [Bitmap].
   *
   * @param input The original image's [Bitmap].
   * @param pointCount The number of points which determine the quality of the lowpoly.
   * @return The generated lowpoly [Bitmap].
   */
  private fun generate(
    input: Bitmap,
    pointCount: Float
  ): Bitmap {
    val newImage = Bitmap.createBitmap(input.width, input.height, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(newImage)
    val paint = Paint()
    paint.isAntiAlias = false
    paint.style = Paint.Style.FILL
    drawTriangles(input.width, input.height, input, pointCount, paint, canvas)
    return newImage
  }

  /**
   * Draws the triangles according to the vertex coordinates on the [Bitmap].
   *
   * @param width The width of the downscaled image.
   * @param height The height of the downscaled image.
   * @param input The downscaled [Bitmap] image.
   * @param pointCount The number of points which determine the quality of the lowpoly.
   * @param paint The [Paint] which is used to draw triangles on to the canvas.
   * @param canvas The [Canvas] on which the triangles are to be drawn.
   */
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
    drawTrianglePath(input, paint, canvas, triangles)
  }

  /**
   * Connects the vertices of the triangles and using a [Path].
   *
   * @param input The downscaled [Bitmap] image.
   * @param paint The [Paint] which is used to draw triangles on to the canvas.
   * @param canvas The [Canvas] on which the triangles are to be drawn.
   * @param triangles The [IntArray] containing the coordinates of the vertices of the triangles.
   */
  private fun drawTrianglePath(input: Bitmap, paint: Paint, canvas: Canvas, triangles: IntArray) {
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

  /**
   * An external function which makes a JNI call to get triangle's coordinates in the image.
   *
   * @return [IntArray] containing pixel coordinates of triangle vertices.
   */
  private external fun getTriangles(
    pixels: IntArray,
    width: Int,
    height: Int,
    point_count: Float
  ): IntArray

}