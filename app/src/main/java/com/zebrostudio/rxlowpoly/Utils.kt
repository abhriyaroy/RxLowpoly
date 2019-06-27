package com.zebrostudio.rxlowpoly

import android.content.Context
import android.view.View
import android.view.View.*
import androidx.annotation.ColorRes

fun Context.colorRes(@ColorRes id: Int) = resources.getColor(id)

fun View.visible() {
  visibility = VISIBLE
}

fun View.gone() {
  visibility = GONE
}

fun View.invisible() {
  visibility = INVISIBLE
}