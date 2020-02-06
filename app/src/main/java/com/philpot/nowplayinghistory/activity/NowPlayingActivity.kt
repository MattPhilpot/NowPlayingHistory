package com.philpot.nowplayinghistory.activity


import android.annotation.SuppressLint
import android.app.NotificationManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.provider.Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS
import android.view.Menu
import android.view.MenuItem
import com.philpot.nowplayinghistory.settings.SettingsBottomSheetDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.Navigation
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.snackbar.Snackbar
import com.philpot.nowplayinghistory.R
import com.philpot.nowplayinghistory.listener.NowPlayingListener
import kotlinx.android.synthetic.main.activity_main2.*
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import org.kodein.di.android.closestKodein
import org.kodein.di.direct
import org.kodein.di.erased.bind
import org.kodein.di.erased.instance
import org.kodein.di.erased.provider


class NowPlayingActivity : AppCompatActivity(),
    KodeinAware,
    SettingsBottomSheetDialog.PermissionAcquirer {

    companion object {
        private val TAG = NowPlayingActivity::class.java.simpleName
        val REQUEST_CODE = 1377
    }

    private val parentKodein by closestKodein()

    override val kodein: Kodein = Kodein.lazy {
        extend(parentKodein)
    }

    private var accessSnackbar: Snackbar? = null
    private var dialog: SettingsBottomSheetDialog? = null


    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.activity_main_options, menu)
        //val searchItem = menu.findItem(R.id.search)

        //enableDisableMenuItem(menu.findItem(R.id.activity_main_option_settings), true)
        //enableDisableMenuItem(searchItem, !deleteModeEnabled)
        //enableDisableMenuItem(searchItem, false)

        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean = when(item.itemId) {
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

        setSupportActionBar(activity_main_toolbar)

        val navController = Navigation.findNavController(this, R.id.activity_main_nav_host)
        activity_acg_main_bottom_nav?.setupWithNavController(navController)

        dialog = SettingsBottomSheetDialog(this, this, direct.instance(), direct.instance())//, EventBus.getDefault())

        checkNotificationService()
    }

    @SuppressLint("MissingSuperCall")
    override fun onDestroy() {
        super.onDestroy()
        dialog?.dismiss()
        dialog = null
    }

    private fun checkNotificationService() {
        if (isNotifyServiceEnabled()) {
            hideNotificationAccessDialog()
        } else {
            showNotificationAccessDialog()
        }
    }

    private fun isNotifyServiceEnabled(): Boolean {
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        return notificationManager.isNotificationListenerAccessGranted(ComponentName(packageName, NowPlayingListener::class.java.name))
    }

    private fun showNotificationAccessDialog() {
        accessSnackbar?.let {
            if (it.isShown) {
                return
            }
            it.show()
            return
        }

        accessSnackbar = Snackbar.make(
            activity_main_coordinator_root,
            R.string.activity_main_needs_access,
            Snackbar.LENGTH_INDEFINITE)
            .setAction(android.R.string.ok) {
                startActivityForResult(Intent(ACTION_NOTIFICATION_LISTENER_SETTINGS), REQUEST_CODE)
        }.apply { show() }
    }

    private fun hideNotificationAccessDialog() {
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
