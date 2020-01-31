package com.philpot.nowplayinghistory.activity


import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.provider.Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS
import android.support.design.widget.Snackbar
import com.github.salomonbrys.kodein.android.KodeinActivity
import com.philpot.nowplayinghistory.R
import com.philpot.nowplayinghistory.model.HistoryItem
import com.philpot.nowplayinghistory.util.RecyclerViewInitializer
import kotlinx.android.synthetic.main.activity_main.*
import android.view.Menu
import android.view.MenuItem
import com.philpot.nowplayinghistory.settings.SettingsBottomSheetDialog
import android.content.Context
import android.content.SharedPreferences
import android.support.v4.content.ContextCompat
import android.support.v4.view.ViewPager
import com.github.salomonbrys.kodein.*
import org.greenrobot.eventbus.EventBus


class NowPlayingActivity : KodeinActivity(), NowPlayingController.NowPlayingView, SettingsBottomSheetDialog.PermissionAcquirer {

    companion object {
        private val TAG = NowPlayingActivity::class.java.simpleName
        val REQUEST_CODE = 113377
    }

    override fun provideOverridingModule() = Kodein.Module {
        bind<NowPlayingController>() with provider {
            NowPlayingController(this@NowPlayingActivity)
        }

        /*
        bind<NowPlayingListAdapter>() with provider {
            NowPlayingListAdapter(instance())
        }
        */

        bind<NowPlayingPagerAdapter>() with provider {
            NowPlayingPagerAdapter(fragmentManager)
        }
    }

    private var accessSnackbar: Snackbar? = null

    private val controller: NowPlayingController? by injector.instance()
    private val pagerAdapter: NowPlayingPagerAdapter by injector.instance()

    private var dialog: SettingsBottomSheetDialog? = null

    private fun enableDisableMenuItem(item: MenuItem?, enable: Boolean) {
        item?.let {
            it.isEnabled = enable
            it.isVisible = enable
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.activity_main_options, menu)
        val searchItem = menu.findItem(R.id.search)

        enableDisableMenuItem(menu.findItem(R.id.activity_main_option_settings), true)
        //enableDisableMenuItem(searchItem, !deleteModeEnabled)
        enableDisableMenuItem(searchItem, false)

        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean = when(item?.itemId) {
        R.id.activity_main_option_settings -> {
            showMenu()
            true
        }
        else -> {
            super.onOptionsItemSelected(item)
        }
    }

    @SuppressLint("MissingSuperCall")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setActionBar(activity_main_toolbar)

        //listAdapter.lastFmEnabled = preferences.getBoolean(Preferences.LastFmIntegration.value, false)

        activity_main_viewpager?.adapter = pagerAdapter
        activity_bottom_navigation_view?.let { navView ->
            navView.setOnNavigationItemSelectedListener {
                if (it.itemId != navView.selectedItemId) {
                    when (it.itemId) {
                        R.id.activity_bottom_navigation_menu_history -> showPage(0)
                        else -> showPage(1)
                    }
                }
                true
            }
        }

        activity_main_viewpager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(state: Int) { /* do nothing */ }
            override fun onPageSelected(position: Int) {
                pagerAdapter.updateItemAt(position)
            }

            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
                title = getString(pagerAdapter.getTitle(position))
            }
        })

        dialog = SettingsBottomSheetDialog(this, this, EventBus.getDefault())
    }

    private fun showPage(position: Int) {
        activity_main_viewpager?.currentItem = position
    }

    override fun onResume() {
        super.onResume()
        controller?.onResume(this)
    }

    @SuppressLint("MissingSuperCall")
    override fun onDestroy() {
        super.onDestroy()
        controller?.destroy()
        dialog?.dismiss()
        dialog = null
    }

    override fun showNotificationAccessDialog() {
        accessSnackbar?.let {
            if (it.isShown) {
                return
            }
            it.show()
            return
        }

        accessSnackbar = Snackbar.make(activity_main_list_root, R.string.activity_main_needs_access, Snackbar.LENGTH_INDEFINITE).setAction("Okay") {
            startActivityForResult(Intent(ACTION_NOTIFICATION_LISTENER_SETTINGS), REQUEST_CODE)
        }
        accessSnackbar?.show()
    }

    override fun hideNotificationAccessDialog() {
        accessSnackbar?.dismiss()
    }

    private fun showMenu() {
        dialog?.setValuesFromPreferences()
        dialog?.show()
    }

    override fun askForPermission(permission: String) {
        requestPermissions(arrayOf(permission), 1337)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        dialog?.onPermissionResults(permissions, grantResults)
    }
}
