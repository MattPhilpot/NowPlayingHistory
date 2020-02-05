package com.philpot.nowplayinghistory.activity


import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.provider.Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS
import kotlinx.android.synthetic.main.activity_main.*
import android.view.Menu
import android.view.MenuItem
import com.philpot.nowplayinghistory.settings.SettingsBottomSheetDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.Navigation
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.snackbar.Snackbar
import com.philpot.nowplayinghistory.R
import kotlinx.android.synthetic.main.activity_main2.*
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import org.kodein.di.android.closestKodein


class NowPlayingActivity : AppCompatActivity(),
    KodeinAware,
    NowPlayingController.NowPlayingView,
    SettingsBottomSheetDialog.PermissionAcquirer {

    companion object {
        private val TAG = NowPlayingActivity::class.java.simpleName
        val REQUEST_CODE = 113377
    }

    private val parentKodein by closestKodein()

    override val kodein: Kodein = Kodein.lazy {
        extend(parentKodein)
    }

    private var accessSnackbar: Snackbar? = null

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
        setContentView(R.layout.activity_main2)

        setActionBar(activity_main_toolbar)

        val navController = Navigation.findNavController(this, R.id.activity_main_nav_host)
        activity_acg_main_bottom_nav?.setupWithNavController(navController)

        dialog = SettingsBottomSheetDialog(this, this)//, EventBus.getDefault())
    }

    private fun showPage(position: Int) {
        activity_main_viewpager?.currentItem = position
    }


    @SuppressLint("MissingSuperCall")
    override fun onDestroy() {
        super.onDestroy()
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

        accessSnackbar = Snackbar.make(
            activity_main_list_root,
            R.string.activity_main_needs_access,
            Snackbar.LENGTH_INDEFINITE)
            .setAction(android.R.string.ok) {
                startActivityForResult(Intent(ACTION_NOTIFICATION_LISTENER_SETTINGS), REQUEST_CODE)
        }.apply { show() }
    }

    override fun hideNotificationAccessDialog() {
        accessSnackbar?.dismiss()
    }

    private fun showMenu() {
        dialog?.apply {
            setValuesFromPreferences()
            show()
        }
    }

    override fun askForPermission(permission: String) {
        requestPermissions(arrayOf(permission), 1337)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        dialog?.onPermissionResults(permissions, grantResults)
    }
}
