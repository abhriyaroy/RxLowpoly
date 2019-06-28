package com.zebrostudio.rxlowpoly.fragments

import android.graphics.Bitmap
import android.view.View
import com.zebrostudio.rxlowpoly.R
import com.zebrostudio.rxlowpoly.RxLowpoly
import io.reactivex.Single
import kotlinx.android.synthetic.main.fragment_bitmap_async.view.*

class BitmapAsyncFragment : BaseFragment() {

  override fun configureView(view: View) {
    view.toolbarFragment.title = "Bitmap Async Example"
    view.imageView.setImageDrawable(resources.getDrawable(R.drawable.captain))
    view.lowpolyButton.setOnClickListener {
      if (permissionChecker.isStoragePermissionAvailable(context!!)) {

      } else {
        requestPermission()
      }
    }
  }

  private fun convertBitmapToLowpolyAsyncWithFileOutput(bitmap: Bitmap): Single<Bitmap> {
    return RxLowpoly.with(activity!!.applicationContext)
      .input(bitmap)
      .overrideScaling(downScalingFactor, maximumWidth)
      .quality(quality)
      .output(file)
      .generateAsync()
  }

}
