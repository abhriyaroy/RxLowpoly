package com.zebrostudio.rxlowpoly

import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.afollestad.materialdialogs.MaterialDialog
import com.bumptech.glide.Glide
import com.mlsdev.rximagepicker.RxImagePicker
import com.mlsdev.rximagepicker.Sources
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

  private var isFileChosen = false
  private lateinit var imageUri: Uri
  private lateinit var compositeDisposable: CompositeDisposable
  private var lowpolyWaitLoader: MaterialDialog? = null

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    compositeDisposable = CompositeDisposable()
    setContentView(R.layout.activity_main)
    attachClickListeners()
    imageView.setImageDrawable(resources.getDrawable(R.drawable.captain))
  }

  override fun onDestroy() {
    if (!compositeDisposable.isDisposed) {
      compositeDisposable.dispose()
    }
    super.onDestroy()
  }

  private fun attachClickListeners() {
    chooseImageButton.setOnClickListener {
      pickFile()
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe {
          Glide.with(this)
            .load(it)
            .into(imageView)
          isFileChosen = true
          imageUri = it
          convertButton.isEnabled = true
        }
    }

    convertButton.setOnClickListener {
      if (!isFileChosen) {
        generateLowpolyImageFromDrawable()
      } else {
        generateLowpolyImageFromUri()
      }
    }
  }

  private fun pickFile(): Observable<Uri> {
    return RxImagePicker.with(supportFragmentManager).requestImage(Sources.GALLERY)
  }

  private fun generateLowpolyImageFromDrawable() {

    RxLowpoly.with(applicationContext)
      .input(R.drawable.captain)
      .quaity(Quality.VERY_HIGH)
      .overrideScaling(5048)
      .generateAsync()
      .observeOn(AndroidSchedulers.mainThread())
      .doOnSubscribe {
        showConvertingImageLoader()
        compositeDisposable.add(it)
      }
      .subscribe({
        // show image
        showResult(it)
      }, {
        // show error
        lowpolyWaitLoader?.dismiss()
      })
  }

  private fun generateLowpolyImageFromUri() {
    RxLowpoly.with(applicationContext)
      .input(imageUri)
      .generateAsync()
      .observeOn(AndroidSchedulers.mainThread())
      .doOnSubscribe {
        showConvertingImageLoader()
        compositeDisposable.add(it)
      }.subscribe({
        // show image
        showResult(it)
      }, {
        // show error
        println("error ${it.message}")
        lowpolyWaitLoader?.dismiss()
      })
  }

  private fun showConvertingImageLoader() {
    lowpolyWaitLoader = MaterialDialog.Builder(this)
      .widgetColor(colorRes(R.color.colorWhite))
      .contentColor(colorRes(R.color.colorWhite))
      .content(getString(R.string.lowpoly_wait_loader_message))
      .backgroundColor(colorRes(R.color.colorPrimary))
      .progress(true, 0)
      .progressIndeterminateStyle(false)
      .cancelable(false)
      .build()

    lowpolyWaitLoader?.show()
  }

  private fun showResult(bitmap: Bitmap) {
    Glide.with(this)
      .load(bitmap)
      .into(imageView)
    convertButton.isEnabled = false
    lowpolyWaitLoader?.dismiss()
  }

}
