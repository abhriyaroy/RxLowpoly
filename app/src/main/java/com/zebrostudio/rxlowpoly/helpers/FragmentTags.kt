package com.zebrostudio.rxlowpoly.helpers

enum class FragmentTags(val tag: String) {
  BITMAP_ASYNC("bitmapasync"),
  BITMAP_SYNC("bitmapsync"),
  DRAWABLE_ASYNC("drawableasync"),
  DRAWABLE_SYNC("drawablesync"),
  FILE_ASYNC("fileasync"),
  FILE_SYNC("filesync"),
  URI_ASYNC("uriasync"),
  URI_SYNC("urisync")
}