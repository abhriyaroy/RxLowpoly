package com.zebrostudio.rxlowpoly.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.zebrostudio.rxlowpoly.MainActivity
import com.zebrostudio.rxlowpoly.R
import com.zebrostudio.rxlowpoly.fragments.FragmentTags.BITMAPASYNC
import kotlinx.android.synthetic.main.fragment_chooser.*
import kotlinx.android.synthetic.main.toolbar_layout.*

class ChooserFragment : Fragment() {

  override fun onCreateView(
    inflater: LayoutInflater, container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
    return inflater.inflate(R.layout.fragment_chooser, container, false)
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    (activity as AppCompatActivity).setSupportActionBar(toolbar)
    setupClickListeners()
  }

  private fun setupClickListeners() {
    bitmapAsyncButton.setOnClickListener {
      showFragment(BitmapAsyncFragment(), BITMAPASYNC.tag)
    }
    bitmapSyncButton.setOnClickListener {
      showFragment(BitmapSyncFragment(), BITMAPASYNC.tag)
    }
  }

  private fun showFragment(fragment: Fragment, tag: String) {
    with((activity as MainActivity).supportFragmentManager.beginTransaction()) {
      replace(R.id.fragmentContainer, fragment, tag)
      addToBackStack(tag)
      commit()
    }
  }

}
