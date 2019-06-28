package com.zebrostudio.rxlowpoly

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.View.*
import androidx.annotation.ColorRes
import androidx.annotation.StringRes
import com.google.android.material.textfield.TextInputEditText

fun Context.colorRes(@ColorRes id: Int) = resources.getColor(id)
fun Context.stringRes(@StringRes id: Int) = getString(id)


fun View.visible() {
  visibility = VISIBLE
}

fun View.gone() {
  visibility = GONE
}

fun View.invisible() {
  visibility = INVISIBLE
}

private class TextChangeListener(
  private val afterTextChange: (editable: Editable?) -> Unit,
  private val beforeTextChanged: (text: CharSequence?) -> Unit,
  private val onTextChanged: (text: CharSequence?) -> Unit
) : TextWatcher {
  override fun afterTextChanged(editable: Editable?) {
    afterTextChange(editable)
  }

  override fun beforeTextChanged(text: CharSequence?, p1: Int, p2: Int, p3: Int) {
    beforeTextChanged(text)
  }

  override fun onTextChanged(text: CharSequence?, p1: Int, p2: Int, p3: Int) {
    onTextChanged(text)
  }
}

fun TextInputEditText.listenToTextChange(
  afterTextChanged: (editable: Editable?) -> Unit = {},
  beforeTextChanged: (text: CharSequence?) -> Unit = {},
  onTextChanged: (text: CharSequence?) -> Unit = {}
) = addTextChangedListener(TextChangeListener(afterTextChanged, beforeTextChanged, onTextChanged))