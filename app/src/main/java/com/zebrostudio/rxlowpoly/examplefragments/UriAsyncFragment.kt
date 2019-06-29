package com.zebrostudio.rxlowpoly.examplefragments

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.net.Uri
import android.view.View
import com.zebrostudio.rxlowpoly.*
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_example.view.*
import java.io.File

private const val SAVE_FILE_NAME = "UriAsyncExample"
private const val CHOOSE_URI_BUTTON_TEXT = "CHOOSE IMAGE URI"
private const val CONVERT_TO_LOWPOLY_TEXT = "CONVERT TO LOWPOLY"

class UriAsyncFragment : BaseFragment() {

  private var uri: Uri? = null

  override fun configureView(view: View) {
    view.toolbarFragment.title = context!!.stringRes(R.string.uri_async_example_fragment_name)
    disableAllOperations(view)
    view.lowpolyButton.text = CHOOSE_URI_BUTTON_TEXT
    view.lowpolyButton.setOnClickListener {
      if (permissionChecker.isStoragePermissionAvailable(context!!)) {
        if (view.lowpolyButton.text == CHOOSE_URI_BUTTON_TEXT) {
          getImageFile(view)
        } else {
          getLowpolyImage(view)
        }
      } else {
        requestPermission()
      }
    }
  }

  @SuppressLint("CheckResult")
  private fun getImageFile(view: View) {
    storageHelper.getInputImageUriSingle(activity!!.supportFragmentManager)
      .subscribeOn(Schedulers.io())
      .observeOn(AndroidSchedulers.mainThread())
      .subscribe({ it ->
        uri = it
        enableAllOperations(view)
        view.lowpolyButton.text = CONVERT_TO_LOWPOLY_TEXT
        context!!.showToast("Uri imported! Please click on convert to lowpoly")
      }, {
        context!!.showToast("Please choose a file to proceed")
      })
  }

  private fun getLowpolyImage(view: View) {
    disposable = if (shouldSaveToFile) {
      getLowpolyInFile()
    } else {
      getLowpolyInUri()
    }.observeOn(AndroidSchedulers.mainThread())
      .doOnSubscribe {
        showMaterialDialog()
      }.subscribe({
        view.imageView.setImageBitmap(it)
        materialDialog.dismiss()
        context!!.showToast(SUCCESS_TOAST_MESSAGE)
        view.lowpolyButton.text = CHOOSE_URI_BUTTON_TEXT
        disableAllOperations(view)
      }, {
        materialDialog.dismiss()
        context!!.showToast(ERROR_TOAST_MESSAGE + "${it.message}")
      })
  }

  private fun getLowpolyInFile(): Single<Bitmap> {
    return storageHelper.getFileToSaveImage(SAVE_FILE_NAME)
      .flatMap {
        convertImageUriToLowpolyAsyncWithFileOutput(uri!!, it)
      }
  }

  private fun getLowpolyInUri(): Single<Bitmap> {
    return storageHelper.getUriToSaveImage(SAVE_FILE_NAME)
      .flatMap {
        convertImageUriToLowpolyAsyncWithUriOutput(uri!!, it)
      }
  }

  private fun convertImageUriToLowpolyAsyncWithFileOutput(
    inputUri: Uri,
    outputFile: File
  ): Single<Bitmap> {
    return RxLowpoly.with(activity!!.applicationContext)
      .input(inputUri)
      .overrideScaling(downScalingFactor, maximumWidth)
      .quality(quality)
      .output(outputFile)
      .generateAsync()
  }

  private fun convertImageUriToLowpolyAsyncWithUriOutput(
    inputUri: Uri,
    outputUri: Uri
  ): Single<Bitmap> {
    return RxLowpoly.with(activity!!.applicationContext)
      .input(inputUri)
      .overrideScaling(downScalingFactor, maximumWidth)
      .quality(quality)
      .output(outputUri)
      .generateAsync()
  }

  private fun enableAllOperations(view: View) {
    view.saveToFile.enable()
    view.saveToUri.enable()
    view.spinner.enable()
    view.downScalingFactorTextLayout.enable()
    view.maximumWidthTextLayout.enable()
  }

  private fun disableAllOperations(view: View) {
    view.saveToFile.disable()
    view.saveToUri.disable()
    view.spinner.disable()
    view.downScalingFactorTextLayout.disable()
    view.maximumWidthTextLayout.disable()
  }
}