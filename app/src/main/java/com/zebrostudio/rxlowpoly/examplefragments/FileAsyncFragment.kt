package com.zebrostudio.rxlowpoly.examplefragments

import android.view.View
import kotlinx.android.synthetic.main.fragment_example.view.*

class FileAsyncFragment : BaseFragment() {
  override fun configureView(view: View) {
    view.toolbarFragment.title = "File Async Example"
  }
}