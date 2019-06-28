package com.zebrostudio.rxlowpoly.fragments

import android.Manifest
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.afollestad.materialdialogs.MaterialDialog
import com.zebrostudio.rxlowpoly.*
import kotlinx.android.synthetic.main.fragment_bitmap_async.view.*
import java.io.File

private const val REQUEST_CODE = 1000

abstract class BaseFragment : Fragment() {
  internal lateinit var materialDialog: MaterialDialog
  internal lateinit var file: File
  internal lateinit var uri: Uri
  internal val permissionChecker: PermissionChecker = PermissionCheckerImpl()
  internal val bitmapHelper: BitmapHelper = BitmapHelperImpl()
  internal val storageHelper: StorageHelper = StorageHelperImpl()
  internal val qualityList: List<String> =
    mutableListOf("Very High", "High", "Medium", "Low", "Very Low")
  internal var quality: Quality = Quality.VERY_HIGH
  internal var downScalingFactor = 1f
  internal var maximumWidth = 1024
  internal var shouldSaveToFile = false

  override fun onCreateView(
    inflater: LayoutInflater, container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
    return inflater.inflate(R.layout.fragment_bitmap_async, container, false)
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    (activity as AppCompatActivity).setSupportActionBar(view.toolbarFragment!!)
    setupSpinner(view)
    setupDownScalingFactor(view)
    setupMaximumWidth(view)
    setUpRadioGroup(view)
    configureView(view)
  }

  abstract fun configureView(view: View)

  internal fun setupSpinner(view: View) {
    val dataAdapter =
      ArrayAdapter<String>(context, android.R.layout.simple_spinner_dropdown_item, qualityList)
    view.spinner.adapter = dataAdapter
    view.spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
      override fun onNothingSelected(parent: AdapterView<*>?) {

      }

      override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        quality = when (position) {
          0 -> Quality.VERY_HIGH
          1 -> Quality.HIGH
          2 -> Quality.MEDIUM
          3 -> Quality.LOW
          else -> Quality.VERY_LOW
        }
      }
    }
  }

  internal fun setupDownScalingFactor(view: View) {
    with(view.downScalingFactorEditText) {
      setText(downScalingFactor.toString())
      listenToTextChange(onTextChanged = {
        try {
          view.downScalingFactorTextLayout.error = null
          val value: Float = it.toString().toFloat()
          downScalingFactor = value
        } catch (exception: NumberFormatException) {
          view.downScalingFactorTextLayout.error = "Please enter a valid float number"
        }
      })
    }
  }

  internal fun setupMaximumWidth(view: View) {
    with(view.maximumWidthEditText) {
      setText(maximumWidth.toString())
      listenToTextChange(onTextChanged = {
        try {
          view.maximumWidthTextLayout.error = null
          val value: Int = it.toString().toInt()
          maximumWidth = value
        } catch (exception: NumberFormatException) {
          view.maximumWidthTextLayout.error = "Please enter an integer"
        }
      })
    }
  }

  internal fun setUpRadioGroup(view: View) {
    view.radioGroup.setOnCheckedChangeListener { _, checkedId ->
      shouldSaveToFile = checkedId == R.id.saveToFile
    }
  }

  internal fun showMaterialDialog(text: String) {
    materialDialog = MaterialDialog.Builder(context!!)
      .backgroundColor(context!!.colorRes(R.color.colorPrimary))
      .widgetColor(context!!.colorRes(R.color.colorWhite))
      .contentColor(context!!.colorRes(R.color.colorWhite))
      .content(text)
      .progressIndeterminateStyle(false)
      .progress(true, Int.MAX_VALUE)
      .build()
    materialDialog.show()
  }

  internal fun requestPermission() {
    requestPermissions(
      arrayOf(
        Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.WRITE_EXTERNAL_STORAGE
      ), REQUEST_CODE
    )
  }
}