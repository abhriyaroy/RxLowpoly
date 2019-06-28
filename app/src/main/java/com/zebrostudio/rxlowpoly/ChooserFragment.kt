package com.zebrostudio.rxlowpoly

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.zebrostudio.rxlowpoly.examplefragments.*
import com.zebrostudio.rxlowpoly.examplefragments.FragmentTags.*
import kotlinx.android.synthetic.main.fragment_chooser.*
import kotlinx.android.synthetic.main.fragment_chooser.view.*

class ChooserFragment : Fragment() {

  override fun onCreateView(
    inflater: LayoutInflater, container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
    return inflater.inflate(R.layout.fragment_chooser, container, false)
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    (activity as AppCompatActivity).setSupportActionBar(view.toolbar)
    setupClickListeners()
  }

  private fun setupClickListeners() {
    bitmapAsyncButton.setOnClickListener {
      showFragment(BitmapAsyncFragment(), BITMAPASYNC.tag)
    }
    bitmapSyncButton.setOnClickListener {
      showFragment(BitmapSyncFragment(), BITMAPSYNC.tag)
    }
    drawableAsyncButton.setOnClickListener {
      showFragment(DrawableAsyncFragment(), DRAWABLEASYNC.tag)
    }
    drawableSyncButton.setOnClickListener {
      showFragment(DrawableSyncFragment(), DRAWABLESYNC.tag)
    }
    fileAsyncButton.setOnClickListener {
      showFragment(FileAsyncFragment(), FILEASYNC.tag)
    }
    fileSyncButton.setOnClickListener {
      showFragment(FileSyncFragment(), FILESYNC.tag)
    }
    uriAsyncButton.setOnClickListener {
      showFragment(UriAsyncFragment(), URIASYNC.tag)
    }
    uriSyncButton.setOnClickListener {
      showFragment(UriSyncFragment(), URISYNC.tag)
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
