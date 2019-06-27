package com.zebrostudio.rxlowpoly.internal.exceptions

class WrongContextException(
  override val message: String = "Context is not application context"
) : Exception()