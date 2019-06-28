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
import kotlinx.android.synthetic.main.fragment_example.view.*
import java.io.File

private const val SAVE_FILE_NAME = "FileAsyncExample"

class FileAsyncFragment : BaseFragment() {

  override fun configureView(view: View) {
    view.toolbarFragment.title = context!!.stringRes(R.string.file_async_example_fragment_name)
    view.lowpolyButton.setOnClickListener {
      if (permissionChecker.isStoragePermissionAvailable(context!!)) {
        getLowpolyImage(view)
      } else {
        requestPermission()
      }
    }
  }

  private fun getLowpolyImage(view: View) {
    disposable = storageHelper.getInputImageFileSingle(activity!!.supportFragmentManager)
      .flatMap {
        if (shouldSaveToFile) {
          getLowpolyInFile(it)
        } else {
          getLowpolyInUri(it)
        }
      }.observeOn(AndroidSchedulers.mainThread())
      .doOnSubscribe {
        showMaterialDialog()
      }.subscribe({
        view.imageView.setImageBitmap(it)
        materialDialog.dismiss()
        context!!.showToast(SUCCESS_TOAST_MESSAGE)
      }, {
        println(it.message)
        materialDialog.dismiss()
        context!!.showToast(ERROR_TOAST_MESSAGE + "${it.message}")
      })
  }

  private fun getLowpolyInFile(inputFile: File): Single<Bitmap> {
    return storageHelper.getFileToSaveImage(SAVE_FILE_NAME)
      .flatMap {
        convertImageFileToLowpolyAsyncWithFileOutput(inputFile, it)
      }
  }

  private fun getLowpolyInUri(inputFile: File): Single<Bitmap> {
    return storageHelper.getUriToSaveImage(SAVE_FILE_NAME)
      .flatMap {
        convertImageFileToLowpolyAsyncWithUriOutput(inputFile, it)
      }
  }

  private fun convertImageFileToLowpolyAsyncWithFileOutput(
    inputFile: File,
    outputFile: File
  ): Single<Bitmap> {
    return RxLowpoly.with(activity!!.applicationContext)
      .input(inputFile)
      .overrideScaling(downScalingFactor, maximumWidth)
      .quality(quality)
      .output(outputFile)
      .generateAsync()
  }

  private fun convertImageFileToLowpolyAsyncWithUriOutput(
    inputFile: File,
    outputUri: Uri
  ): Single<Bitmap> {
    return RxLowpoly.with(activity!!.applicationContext)
      .input(inputFile)
      .overrideScaling(downScalingFactor, maximumWidth)
      .quality(quality)
      .output(outputUri)
      .generateAsync()
  }
}