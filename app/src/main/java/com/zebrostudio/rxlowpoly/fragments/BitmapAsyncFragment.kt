package com.zebrostudio.rxlowpoly.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.zebrostudio.rxlowpoly.R
import kotlinx.android.synthetic.main.fragment_bitmap_async.*
import kotlinx.android.synthetic.main.toolbar_layout.*

class BitmapAsyncFragment : Fragment() {

  override fun onCreateView(
    inflater: LayoutInflater, container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
    return inflater.inflate(R.layout.fragment_bitmap_async, container, false)
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    (activity as AppCompatActivity).setSupportActionBar(toolbar)
    toolbar.title = "Lowpoly from Bitmap"
    imageView.setImageDrawable(resources.getDrawable(R.drawable.captain))
    lowpolyButton.setOnClickListener {
      convertImageToLowpoly()
    }
  }

  private fun convertImageToLowpoly(){

  }

}
