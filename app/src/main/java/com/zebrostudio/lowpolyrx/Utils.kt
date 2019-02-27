package com.zebrostudio.lowpolyrx

import android.content.Context
import android.support.annotation.ColorRes
import android.support.annotation.DrawableRes

fun Context.colorRes(@ColorRes id: Int) = resources.getColor(id)

fun Context.drawableRes(@DrawableRes id: Int) = resources.getDrawable(id)!!