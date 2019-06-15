package com.zebrostudio.lowpolyrx

import android.content.Context
import android.support.annotation.ColorRes

fun Context.colorRes(@ColorRes id: Int) = resources.getColor(id)