package com.zebrostudio.rxlowpoly

/**
 * The various quality configurations of RxLowpoly.
 */
enum class Quality(val pointCount: Float) {
  VERY_HIGH(8000f),
  HIGH(4000f),
  MEDIUM(1000f),
  LOW(500f),
  VERY_LOW(100f)
}