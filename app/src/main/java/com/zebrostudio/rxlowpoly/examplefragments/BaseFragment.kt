package com.zebrostudio.rxlowpoly.examplefragments

import android.Manifest
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
import com.zebrostudio.rxlowpoly.helpers.*
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.fragment_example.view.*

const val SUCCESS_TOAST_MESSAGE = "Wohooo...Lowpoly image has been generated and saved!"
const val ERROR_TOAST_MESSAGE = "Err..."
private const val REQUEST_CODE = 1000

abstract class BaseFragment : Fragment() {
  internal lateinit var materialDialog: MaterialDialog
  internal val permissionChecker: PermissionChecker = PermissionCheckerImpl()
  internal val bitmapHelper: BitmapHelper = BitmapHelperImpl()
  internal val storageHelper: StorageHelper = StorageHelperImpl()
  internal var quality: Quality = Quality.VERY_HIGH
  internal var downScalingFactor = 1f
  internal var maximumWidth = 1024
  internal var shouldSaveToFile = true
  internal var disposable: Disposable? = null
  private val qualityList: List<String> =
    mutableListOf("Very High", "High", "Medium", "Low", "Very Low")

  override fun onCreateView(
    inflater: LayoutInflater, container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
    return inflater.inflate(R.layout.fragment_example, container, false)
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

  override fun onDestroy() {
    if (disposable?.isDisposed == false) {
      disposable?.dispose()
    }
    super.onDestroy()
  }

  abstract fun configureView(view: View)

  private fun setupSpinner(view: View) {
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

  private fun setupDownScalingFactor(view: View) {
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

  private fun setupMaximumWidth(view: View) {
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

  private fun setUpRadioGroup(view: View) {
    view.saveToFile.isChecked = true
    view.radioGroup.setOnCheckedChangeListener { _, checkedId ->
      shouldSaveToFile = checkedId == R.id.saveToFile
    }
  }

  internal fun showMaterialDialog() {
    materialDialog = MaterialDialog.Builder(context!!)
      .backgroundColor(context!!.colorRes(R.color.colorPrimary))
      .widgetColor(context!!.colorRes(R.color.colorWhite))
      .contentColor(context!!.colorRes(R.color.colorWhite))
      .content(context!!.stringRes(R.string.lowpoly_wait_loader_message))
      .cancelable(false)
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