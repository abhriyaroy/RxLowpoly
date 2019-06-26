package com.zebrostudio.rxlowpoly.internal.builder

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.net.Uri
import com.zebrostudio.rxlowpoly.Quality
import com.zebrostudio.rxlowpoly.internal.builder.InputType.*
import com.zebrostudio.rxlowpoly.internal.exceptions.StoragePermissionNotAvailableException
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import java.io.File

class RxLowpolyBuilder {
  private lateinit var context: Context
  private lateinit var inputType: InputType
  private lateinit var inputBitmap: Bitmap
  private lateinit var inputDrawable: Drawable
  private lateinit var inputFile: File
  private lateinit var inputUri: Uri
  private lateinit var outputFile: File
  private lateinit var outputUri: Uri
  private var quality: Quality = Quality.VERY_HIGH
  private var maxWidth: Int = 1024
  private var downScalingFactor = 1f

  fun init(context: Context): RxLowpolyBuilder {
    this.context = context
    return this
  }

  fun quaity(quality: Quality): RxLowpolyBuilder {
    this.quality = quality
    return this
  }

  fun overrideScaling(downScalingFactor: Float): RxLowpolyBuilder {
    this.downScalingFactor = downScalingFactor
    return this
  }

  fun overrideScaling(maxWidth: Int): RxLowpolyBuilder {
    this.maxWidth = maxWidth
    return this
  }

  fun overrideScaling(downScalingFactor: Float, maxWidth: Int): RxLowpolyBuilder {
    this.downScalingFactor = downScalingFactor
    this.maxWidth = maxWidth
    return this
  }

  fun input(bitmap: Bitmap): RxLowpolyBuilder {
    inputType = BITMAP
    inputBitmap = bitmap
    return this
  }

  fun input(drawable: Drawable): RxLowpolyBuilder {
    inputType = DRAWABLE
    inputDrawable = drawable
    return this
  }

  fun input(file: File): RxLowpolyBuilder {
    inputType = FILE
    inputFile = file
    return this
  }

  fun input(uri: Uri): RxLowpolyBuilder {
    inputType = URI
    inputUri = uri
    return this
  }

  fun output(file: File): RxLowpolyBuilder {
    outputFile = file
    return this
  }

  fun output(uri: Uri): RxLowpolyBuilder {
    outputUri = uri
    return this
  }

  fun generateAsyn(): Single<Bitmap> {
    return getBitmap()
      .subscribeOn(Schedulers.io())
  }

  @Throws(StoragePermissionNotAvailableException::class)
  fun generate(): Bitmap {

  }

  private fun getBitmap(): Single<Bitmap> {
    return Single.create {
      it.onSuccess(generate())
    }
  }
}