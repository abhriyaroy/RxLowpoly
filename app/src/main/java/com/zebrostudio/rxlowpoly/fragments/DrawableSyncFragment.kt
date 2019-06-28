package com.zebrostudio.rxlowpoly.fragments

import android.view.View
import com.zebrostudio.rxlowpoly.R
import kotlinx.android.synthetic.main.fragment_bitmap_async.view.*

class DrawableSyncFragment : BaseFragment() {
  override fun configureView(view: View) {
    view.toolbarFragment.title = "Drawable Sync Example"
    view.imageView.setImageDrawable(resources.getDrawable(R.drawable.captain))
  }
}