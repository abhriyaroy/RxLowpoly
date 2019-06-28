package com.zebrostudio.rxlowpoly.examplefragments

import android.graphics.Bitmap
import android.view.View
import com.zebrostudio.rxlowpoly.R
import com.zebrostudio.rxlowpoly.RxLowpoly
import com.zebrostudio.rxlowpoly.stringRes
import io.reactivex.Single
import kotlinx.android.synthetic.main.fragment_example.view.*

class BitmapAsyncFragment : BaseFragment() {

  override fun configureView(view: View) {
    view.toolbarFragment.title = context!!.stringRes(R.string.bitmap_async_example_fragment_name)
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
