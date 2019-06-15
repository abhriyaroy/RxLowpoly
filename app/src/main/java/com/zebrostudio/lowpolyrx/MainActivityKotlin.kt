package com.zebrostudio.lowpolyrx

import android.graphics.Bitmap
import android.os.Bundle
import android.os.Environment
import android.support.v7.app.AppCompatActivity
import com.afollestad.materialdialogs.MaterialDialog
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.activity_main.imageComparisonView
import java.io.File

class MainActivityKotlin : AppCompatActivity() {

  private lateinit var disposable: Disposable
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

    disposable = generateLowpoly()
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe({
          imageComparisonView.setImages(R.mipmap.captain, it)
          val file = File(Environment.getExternalStorageDirectory(), "Sample18.jpg")
          file.createNewFile()
          file.writeBitmap(it, Bitmap.CompressFormat.PNG, 100)
          lowpolyWaitLoader?.dismiss()
        }, {
          lowpolyWaitLoader?.dismiss()
        })

  }

  override fun onDestroy() {
    if (!disposable.isDisposed) {
      disposable.dispose()
    }
    super.onDestroy()
  }

  private fun generateLowpoly(): Single<Bitmap> {
    return LowPolyRx().getLowPolyImage(this, R.mipmap.captain)
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
