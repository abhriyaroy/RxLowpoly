package com.zebrostudio.rxlowpoly

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_main)
    setupChooserFragment()
  }

  private fun setupChooserFragment() {
    with(supportFragmentManager.beginTransaction()) {
      replace(R.id.fragmentContainer, ChooserFragment())
      commit()
    }
  }

  override fun onRequestPermissionsResult(
    requestCode: Int,
    permissions: Array<out String>,
    grantResults: IntArray
  ) {
    super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    supportFragmentManager.let {
      it.findFragmentById(
        it.getBackStackEntryAt(supportFragmentManager.backStackEntryCount - 1).id
      )
    }.let {
      it?.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }
  }

}
