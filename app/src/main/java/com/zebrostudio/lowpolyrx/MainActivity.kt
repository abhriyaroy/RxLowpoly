package com.zebrostudio.lowpolyrx

import android.arch.lifecycle.Lifecycle
import android.graphics.Bitmap
import android.os.Bundle
import android.os.Environment
import android.support.v7.app.AppCompatActivity
import com.afollestad.materialdialogs.MaterialDialog
import com.uber.autodispose.android.lifecycle.AndroidLifecycleScopeProvider
import com.uber.autodispose.autoDisposable
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import kotlinx.android.synthetic.main.activity_main.imageComparisonView
import java.io.File

class MainActivity : AppCompatActivity() {

  private var lowpolyWaitLoader: MaterialDialog? = null

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_main)

    lowpolyWaitLoader = MaterialDialog.Builder(this)
        .widgetColor(colorRes(R.color.white))
        .contentColor(colorRes(R.color.white))
        .content(getString(R.string.lowpoly_wait_loader_message))
        .backgroundColor(colorRes(R.color.colorPrimary))
        .progress(true, 0)
        .progressIndeterminateStyle(false)
        .cancelable(false)
        .build()

    lowpolyWaitLoader?.show()

    generateLowpoly()
        .observeOn(AndroidSchedulers.mainThread())
        .autoDisposable(AndroidLifecycleScopeProvider.from(this, Lifecycle.Event.ON_DESTROY))
        .subscribe({
          imageComparisonView.setImages(R.mipmap.sample_large, it)
          val file = File(Environment.getExternalStorageDirectory(), "Sample6.jpg")
          file.createNewFile()
          file.writeBitmap(it, Bitmap.CompressFormat.JPEG, 100)
          lowpolyWaitLoader?.dismiss()
        }, {
          lowpolyWaitLoader?.dismiss()
        })

  }

  private fun generateLowpoly(): Single<Bitmap> {
    return LowPolyRx().getLowPolyImage(this, R.mipmap.sample_large)
  }

}

private fun File.writeBitmap(
  bitmap: Bitmap,
  format: Bitmap.CompressFormat,
  quality: Int
) {
  outputStream().use { out ->
    bitmap.compress(format, quality, out)
    out.flush()
  }
}
