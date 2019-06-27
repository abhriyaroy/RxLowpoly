package com.zebrostudio.rxlowpoly.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.zebrostudio.rxlowpoly.R

class BitmapSyncFragment : Fragment() {

  override fun onCreateView(
    inflater: LayoutInflater, container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
    return TextView(activity).apply {
      setText(R.string.hello_blank_fragment)
    }
  }

}
