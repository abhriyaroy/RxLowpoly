package com.zebrostudio.rxlowpoly.examplefragments

import android.graphics.Bitmap
import android.net.Uri
import android.view.View
import com.zebrostudio.rxlowpoly.R
import com.zebrostudio.rxlowpoly.RxLowpoly
import com.zebrostudio.rxlowpoly.showToast
import com.zebrostudio.rxlowpoly.stringRes
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_example.view.*
import java.io.File

private const val SAVE_FILE_NAME = "BitmapSyncExample"

class BitmapSyncFragment : BaseFragment() {

  override fun configureView(view: View) {
    view.toolbarFragment.title = context!!.stringRes(R.string.bitmap_sync_example_fragment_name)
    view.imageView.setImageDrawable(resources.getDrawable(R.drawable.captain))
    view.lowpolyButton.setOnClickListener {
      if (permissionChecker.isStoragePermissionAvailable(context!!)) {
        getLowpolyImage(view)
      } else {
        requestPermission()
      }
    }
  }

  private fun getLowpolyImage(view: View) {
    disposable = if (shouldSaveToFile) {
      getLowpolyInFile()
    } else {
      getLowpolyInUri()
    }.subscribeOn(Schedulers.io())
      .observeOn(AndroidSchedulers.mainThread())
      .doOnSubscribe {
        showMaterialDialog()
      }.subscribe({
        view.imageView.setImageBitmap(it)
        materialDialog.dismiss()
        context!!.showToast(SUCCESS_TOAST_MESSAGE)
      }, {
        materialDialog.dismiss()
        context!!.showToast(ERROR_TOAST_MESSAGE + "${it.message}")
      })
  }

  private fun getLowpolyInFile(): Single<Bitmap> {
    return storageHelper.getFileToSaveImage(SAVE_FILE_NAME)
      .flatMap {
        convertBitmapToLowpolySyncWithFileOutput(
          bitmapHelper.drawableToBitmap(context!!, R.drawable.captain), it
        )
      }
  }

  private fun getLowpolyInUri(): Single<Bitmap> {
    return storageHelper.getUriToSaveImage(SAVE_FILE_NAME)
      .flatMap {
        convertBitmapToLowpolySyncWithUriOutput(
          bitmapHelper.drawableToBitmap(context!!, R.drawable.captain), it
        )
      }
  }

  private fun convertBitmapToLowpolySyncWithFileOutput(
    bitmap: Bitmap,
    file: File
  ): Single<Bitmap> {
    return Single.create {
      val lowpolyBitmap = RxLowpoly.with(activity!!.applicationContext)
        .input(bitmap)
        .overrideScaling(downScalingFactor, maximumWidth)
        .quality(quality)
        .output(file)
        .generate()
      it.onSuccess(lowpolyBitmap)
    }
  }

  private fun convertBitmapToLowpolySyncWithUriOutput(bitmap: Bitmap, uri: Uri): Single<Bitmap> {
    return Single.create {
      val lowpolyBitmap = RxLowpoly.with(activity!!.applicationContext)
        .input(bitmap)
        .overrideScaling(downScalingFactor, maximumWidth)
        .quality(quality)
        .output(uri)
        .generate()
      it.onSuccess(lowpolyBitmap)
    }
  }

}