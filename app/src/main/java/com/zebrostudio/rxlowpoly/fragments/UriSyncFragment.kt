package com.zebrostudio.rxlowpoly.fragments

import android.view.View
import kotlinx.android.synthetic.main.fragment_bitmap_async.view.*

class UriSyncFragment : BaseFragment() {
  override fun configureView(view: View) {
    view.toolbarFragment.title = "Uri Sync Example"
  }
}