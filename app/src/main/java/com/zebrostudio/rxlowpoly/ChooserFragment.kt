package com.zebrostudio.rxlowpoly

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.zebrostudio.rxlowpoly.examplefragments.*
import com.zebrostudio.rxlowpoly.helpers.FragmentTags.*
import kotlinx.android.synthetic.main.fragment_chooser.*
import kotlinx.android.synthetic.main.fragment_chooser.view.*

private const val REPOSITORY_LINK = "https://github.com/abhriyaroy/RxLowpoly"

class ChooserFragment : Fragment() {

  override fun onCreateView(
    inflater: LayoutInflater, container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
    return inflater.inflate(R.layout.fragment_chooser, container, false)
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    setUpToolbar(view)
    setupClickListeners()
  }

  override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
    inflater?.inflate(R.menu.chooser_fragment_menu, menu)
  }

  override fun onOptionsItemSelected(item: MenuItem?): Boolean {
    if (item?.itemId == R.id.githubRepoMenuItem) {
      startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(REPOSITORY_LINK)))
    }
    return true
  }

  private fun setUpToolbar(view: View) {
    (activity as AppCompatActivity).setSupportActionBar(view.toolbar)
    setHasOptionsMenu(true)
  }

  private fun setupClickListeners() {
    bitmapAsyncButton.setOnClickListener {
      showFragment(BitmapAsyncFragment(), BITMAP_ASYNC.tag)
    }
    bitmapSyncButton.setOnClickListener {
      showFragment(BitmapSyncFragment(), BITMAP_SYNC.tag)
    }
    drawableAsyncButton.setOnClickListener {
      showFragment(DrawableAsyncFragment(), DRAWABLE_ASYNC.tag)
    }
    drawableSyncButton.setOnClickListener {
      showFragment(DrawableSyncFragment(), DRAWABLE_SYNC.tag)
    }
    fileAsyncButton.setOnClickListener {
      showFragment(FileAsyncFragment(), FILE_ASYNC.tag)
    }
    fileSyncButton.setOnClickListener {
      showFragment(FileSyncFragment(), FILE_SYNC.tag)
    }
    uriAsyncButton.setOnClickListener {
      showFragment(UriAsyncFragment(), URI_ASYNC.tag)
    }
    uriSyncButton.setOnClickListener {
      showFragment(UriSyncFragment(), URI_SYNC.tag)
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
