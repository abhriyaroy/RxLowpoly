package com.zebrostudio.rxlowpoly.examplefragments

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.net.Uri
import android.util.Log
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

private const val SAVE_FILE_NAME = "FileAsyncExample"
private const val CHOOSE_FILE_BUTTON_TEXT = "CHOOSE IMAGE FILE"
private const val CONVERT_TO_LOWPOLY_TEXT = "CONVERT TO LOWPOLY"
private const val FILE_IMPORTED_SUCCESS_MESSAGE =
  "File imported! Please click on convert to lowpoly"
private const val FILE_NOT_IMPORTED_ERROR_MESSAGE =
  "File couldn't be imported! Please choose a file to proceed"

class FileAsyncFragment : BaseFragment() {

  private var file: File? = null

  override fun configureView(view: View) {
    view.toolbarFragment.title = context!!.stringRes(R.string.file_async_example_fragment_name)
    disableAllOperations(view)
    view.lowpolyButton.text = CHOOSE_FILE_BUTTON_TEXT
    view.lowpolyButton.setOnClickListener {
      if (permissionChecker.isStoragePermissionAvailable(context!!)) {
        if (view.lowpolyButton.text == CHOOSE_FILE_BUTTON_TEXT) {
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
    storageHelper.getInputImageFileSingle(context!!, activity!!.supportFragmentManager)
      .subscribeOn(Schedulers.io())
      .observeOn(AndroidSchedulers.mainThread())
      .subscribe({ it ->
        file = it
        enableAllOperations(view)
        view.lowpolyButton.text = CONVERT_TO_LOWPOLY_TEXT
        context!!.showToast(FILE_IMPORTED_SUCCESS_MESSAGE)
      }, {
        println(it.message)
        Log.e("error", it.printStackTrace().toString())
        context!!.showToast(FILE_NOT_IMPORTED_ERROR_MESSAGE)
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
        view.lowpolyButton.text = CHOOSE_FILE_BUTTON_TEXT
        disableAllOperations(view)
      }, {
        materialDialog.dismiss()
        context!!.showToast(ERROR_TOAST_MESSAGE + "${it.message}")
      })
  }

  private fun getLowpolyInFile(): Single<Bitmap> {
    return storageHelper.getFileToSaveImage(SAVE_FILE_NAME)
      .flatMap {
        convertImageFileToLowpolyAsyncWithFileOutput(file!!, it)
      }
  }

  private fun getLowpolyInUri(): Single<Bitmap> {
    return storageHelper.getUriToSaveImage(SAVE_FILE_NAME)
      .flatMap {
        convertImageFileToLowpolyAsyncWithUriOutput(file!!, it)
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