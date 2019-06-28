package com.zebrostudio.rxlowpoly.fragments

import android.view.View
import kotlinx.android.synthetic.main.fragment_bitmap_async.view.*

class FileSyncFragment : BaseFragment() {
  override fun configureView(view: View) {
    view.toolbarFragment.title = "File Sync Example"
  }
}