package com.zebrostudio.rxlowpoly.internal.exceptions

class WrongContextException : Exception(){
  override val message: String = "Please use application context"
}