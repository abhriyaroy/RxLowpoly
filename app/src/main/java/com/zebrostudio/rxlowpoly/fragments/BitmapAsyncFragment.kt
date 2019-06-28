package com.zebrostudio.rxlowpoly.fragments

import android.Manifest
import android.graphics.Bitmap
import android.view.View
import com.zebrostudio.rxlowpoly.R
import com.zebrostudio.rxlowpoly.RxLowpoly
import io.reactivex.Single
import kotlinx.android.synthetic.main.fragment_bitmap_async.view.*

private const val REQUEST_CODE = 1000

class BitmapAsyncFragment : BaseFragment() {

  private fun requestPermission() {
    requestPermissions(
      arrayOf(
        Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.WRITE_EXTERNAL_STORAGE
      ), REQUEST_CODE
    )
  }

  override fun configureView(view: View) {
   
  }

  private fun configureBitmapAsyncView(view: View) {
    view.toolbarFragment.title = "Bitmap Async Example"
    view.imageView.setImageDrawable(resources.getDrawable(R.drawable.captain))
    view.lowpolyButton.setOnClickListener {
      if (permissionChecker.isStoragePermissionAvailable(context!!)) {

      } else {
        requestPermission()
      }
    }
  }

  private fun configureBitmapSyncView(view: View) {
    view.toolbarFragment.title = "Bitmap Sync Example"
    view.imageView.setImageDrawable(resources.getDrawable(R.drawable.captain))
  }

  private fun configureDrawableAsyncView(view: View) {
    view.toolbarFragment.title = "Drawable Async Example"
    view.imageView.setImageDrawable(resources.getDrawable(R.drawable.captain))
  }

  private fun configureDrawableSyncView(view: View) {
    view.toolbarFragment.title = "Drawable Sync Example"
    view.imageView.setImageDrawable(resources.getDrawable(R.drawable.captain))
  }

  private fun configureFileAsyncView(view: View) {
    view.toolbarFragment.title = "File Async Example"
  }

  private fun configureFileSyncView(view: View) {
    view.toolbarFragment.title = "File Sync Example"
  }

  private fun configureUriAsyncView(view: View) {
    view.toolbarFragment.title = "Uri Async Example"
  }

  private fun configureUriSyncView(view: View) {
    view.toolbarFragment.title = "Uri Sync Example"
  }

  private fun convertBitmapToLowpolyAsyncWithFileOutput(bitmap: Bitmap): Single<Bitmap> {
    return RxLowpoly.with(activity!!.applicationContext)
      .input(bitmap)
      .overrideScaling(downScalingFactor, maximumWidth)
      .quality(quality)
      .output(file)
      .generateAsync()
  }

  private fun convertBitmapToLowpolyAsyncWithUriOutput(bitmap: Bitmap): Single<Bitmap> {
    return RxLowpoly.with(activity!!.applicationContext)
      .input(bitmap)
      .overrideScaling(downScalingFactor, maximumWidth)
      .quality(quality)
      .output(file)
      .generateAsync()
  }

  private fun convertBitmapToLowpolySyncWithFileOutput(bitmap: Bitmap): Bitmap {
    return RxLowpoly.with(activity!!.applicationContext)
      .input(bitmap)
      .overrideScaling(downScalingFactor, maximumWidth)
      .quality(quality)
      .output(file)
      .generate()
  }

  private fun convertBitmapToLowpolySyncWithUriOutput(bitmap: Bitmap): Bitmap {
    return RxLowpoly.with(activity!!.applicationContext)
      .input(bitmap)
      .overrideScaling(downScalingFactor, maximumWidth)
      .quality(quality)
      .output(file)
      .generate()
  }

}
