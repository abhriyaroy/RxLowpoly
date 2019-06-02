package com.zebrostudio.lowpolyrx

import android.arch.lifecycle.Lifecycle
import android.graphics.Bitmap
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.afollestad.materialdialogs.MaterialDialog
import com.bumptech.glide.Glide
import com.uber.autodispose.android.lifecycle.AndroidLifecycleScopeProvider
import com.uber.autodispose.autoDisposable
import com.zebrostudio.lowpolyrxjava.LowPolyRx
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class MainActivity : AppCompatActivity() {

  private val initialLoaderProgress = 0
  private var lowpolyWaitLoader: MaterialDialog? = null
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_main)

    lowpolyWaitLoader = MaterialDialog.Builder(this)
        .widgetColor(colorRes(R.color.colorWhite))
        .contentColor(colorRes(R.color.colorWhite))
        .content(getString(R.string.lowpoly_wait_loader_message))
        .backgroundColor(colorRes(R.color.colorPrimary))
        .progress(true, initialLoaderProgress)
        .progressIndeterminateStyle(false)
        .cancelable(false)
        .build()

    generateLowpoly()
        .subscribeOn(Schedulers.computation())
        .flatMap {
          generateLowpoly()
        }
        .observeOn(AndroidSchedulers.mainThread())
        .doOnSubscribe {
          Glide.with(this)
              .load(R.drawable.sample_large)
              .into(beforeImageView)
          lowpolyWaitLoader?.show()
        }
        .autoDisposable(AndroidLifecycleScopeProvider.from(this, Lifecycle.Event.ON_DESTROY))
        .subscribe({
          Glide.with(this)
              .load(it)
              .into(afterImageView)
          lowpolyWaitLoader?.dismiss()
        }, {
          lowpolyWaitLoader?.dismiss()
        })

  }

  private fun generateLowpoly(): Single<Bitmap> {
    return LowPolyRx().getLowPolyImage(this, R.drawable.sample_large)
  }

}
