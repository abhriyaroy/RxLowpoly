package com.zebrostudio.rxlowpoly

import android.content.Context
import com.zebrostudio.rxlowpoly.internal.builder.RxLowpolyBuilder

object RxLowpoly {

  fun with(applicationContext: Context): RxLowpolyBuilder {
    return RxLowpolyBuilder().init(applicationContext)
  }

}