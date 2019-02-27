package com.zebrostudio.lowpolyrx

import android.arch.lifecycle.Lifecycle
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.Toast
import com.afollestad.materialdialogs.MaterialDialog
import com.uber.autodispose.android.lifecycle.AndroidLifecycleScopeProvider
import com.uber.autodispose.autoDisposable
import com.zebrostudio.lowpolyrxjava.LowPoly
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_main.imageView

class MainActivity : AppCompatActivity() {

  private val initialLoaderProgress = 0
  private var lowpolyWaitLoader: MaterialDialog? = null
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_main)

    lowpolyWaitLoader = MaterialDialog.Builder(this)
      .widgetColor(colorRes(R.color.colorAccent))
      .contentColor(colorRes(R.color.colorBlack))
      .content(getString(R.string.lowpoly_wait_loader_message))
      .backgroundColor(colorRes(R.color.colorPrimary))
      .progress(true, initialLoaderProgress)
      .progressIndeterminateStyle(false)
      .cancelable(false)
      .build()

    getOriginalBitmapFromDrawable()
      .subscribeOn(Schedulers.computation())
      .flatMap {
        generateLowpoly(it)
      }
      .observeOn(AndroidSchedulers.mainThread())
      .doOnSubscribe {
        imageView.setImageDrawable(drawableRes(R.drawable.sample3))
        lowpolyWaitLoader?.show()
      }
      .autoDisposable(AndroidLifecycleScopeProvider.from(this, Lifecycle.Event.ON_DESTROY))
      .subscribe({
        imageView.setImageBitmap(it)
        lowpolyWaitLoader?.dismiss()
      }, {
        Toast.makeText(
          this,
          getString(R.string.lowpoly_generation_error_message),
          Toast.LENGTH_LONG
        ).show()
        lowpolyWaitLoader?.dismiss()
      })

  }

  private fun generateLowpoly(bitmap: Bitmap): Single<Bitmap> {
    return LowPoly.generate(bitmap)
  }

  private fun getOriginalBitmapFromDrawable(): Single<Bitmap> {
    return Single.create {
      it.onSuccess(
        BitmapFactory.decodeResource(
          resources,
          R.drawable.sample3
        )
      )
    }
  }

}
