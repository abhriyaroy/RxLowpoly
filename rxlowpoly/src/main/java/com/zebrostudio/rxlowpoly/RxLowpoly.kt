package com.zebrostudio.rxlowpoly

import android.content.Context
import com.zebrostudio.rxlowpoly.internal.builder.RxLowpolyBuilder

object RxLowpoly {

  /**
   * Entry point of the RxLowpolyBuilder.
   * Application context is used to generate Lowpoly independent of the activity scope.
   *
   *  @param context context.
   *  @return [RxLowpolyBuilder].
   */
  @JvmStatic
  fun with(context: Context): RxLowpolyBuilder {
    return RxLowpolyBuilder().init(context.applicationContext)
  }

}