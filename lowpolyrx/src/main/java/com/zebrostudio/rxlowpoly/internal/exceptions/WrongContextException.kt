package com.zebrostudio.rxlowpoly.internal.exceptions

class WrongContextException(
  override val message: String = "Please use application context"
) : Exception()