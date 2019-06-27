package com.zebrostudio.rxlowpoly

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.zebrostudio.rxlowpoly.fragments.ChooserFragment
import com.zebrostudio.rxlowpoly.fragments.FragmentTags.CHOOSER

class MainActivity : AppCompatActivity() {

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_main)
    setupChooserFragment()
  }

  private fun setupChooserFragment() {
    with(supportFragmentManager.beginTransaction()) {
      replace(R.id.fragmentContainer, ChooserFragment(), CHOOSER.tag)
      addToBackStack(CHOOSER.tag)
      commit()
    }
  }

}
