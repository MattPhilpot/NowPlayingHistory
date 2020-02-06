package com.philpot.nowplayinghistory.settings

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import com.google.android.material.bottomsheet.BottomSheetDialog
import androidx.core.content.ContextCompat
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Switch
import com.github.salomonbrys.kodein.android.appKodein
import com.github.salomonbrys.kodein.instance
import com.philpot.nowplayinghistory.R
import com.philpot.nowplayinghistory.event.SettingsChangedEvent
import com.philpot.nowplayinghistory.model.MusicAppPreference
import com.philpot.nowplayinghistory.model.Preferences
import com.philpot.nowplayinghistory.util.ShortcutHelper
import kotlinx.android.synthetic.main.layout_settings.*
import org.greenrobot.eventbus.EventBus

/**
 * Created by MattPhilpot on 11/1/2017.
 */
class SettingsBottomSheetDialog(context: Context,
                                private val permissionAcquirer: PermissionAcquirer,
                                private val eventBus: EventBus) : BottomSheetDialog(context) {

    private val preferences = context.getSharedPreferences(context.getString(R.string.app_preferences_file), Context.MODE_PRIVATE)
    private val editor = preferences.edit()

    init {
        setContentView(R.layout.layout_settings)
        setValuesFromPreferences()
    }

    fun setValuesFromPreferences() {
        val spinnerArrayAdapter = ArrayAdapter(context, android.R.layout.simple_spinner_item, MusicAppPreference.getAvailableList(context))
        spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        layout_settings_play_song_spinner?.adapter = spinnerArrayAdapter
        layout_settings_play_song_spinner?.setSelection(preferences.getInt(Preferences.CurrentMusicApp.value, 0))
        layout_settings_play_song_spinner?.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(p0: AdapterView<*>?) {
                //do nothing
            }

            override fun onItemSelected(p0: AdapterView<*>?, view: View?, position: Int, id: Long) {
                eventBus.post(SettingsChangedEvent(Preferences.CurrentMusicApp))
                editor.putInt(Preferences.CurrentMusicApp.value, position)
                editor.apply()
                val kodein = appKodein.invoke()
                ShortcutHelper.updateShortcuts(context, kodein.instance(), kodein.instance())
            }
        }

        applyCheckChangeListener(layout_settings_scroll_to_top, Preferences.ScrollToTop)
        doGPSEnableListenerSetup()
        doLastFMCheckCangeListenerSetup()
    }

    private fun applyCheckChangeListener(switch: Switch, pref: Preferences) {
        switch.isChecked = preferences.getBoolean(pref.value, false)
        switch.setOnCheckedChangeListener { _, b ->
            editor.putBoolean(pref.value, b)
            editor.apply()
        }
    }


    private fun doGPSEnableListenerSetup() {
        layout_settings_get_gps.isChecked = preferences.getBoolean(Preferences.GPSEnable.value, false)
        layout_settings_get_gps?.setOnCheckedChangeListener { _, b ->
            if (b) {
                doGPSPermissionCheck()
            } else {
                setGPSEnablePref(b)
            }
        }
    }

    private fun doLastFMCheckCangeListenerSetup() {
        layout_settings_lastfm_integration.isChecked = preferences.getBoolean(Preferences.LastFmIntegration.value, false)
        layout_settings_lastfm_integration.setOnCheckedChangeListener { _, b ->
            eventBus.post(SettingsChangedEvent(Preferences.LastFmIntegration))
            editor.putBoolean(Preferences.LastFmIntegration.value, b)
            editor.apply()
        }
    }

    private fun doGPSPermissionCheck() {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            setGPSEnablePref(true)
            return
        }
        permissionAcquirer.askForPermission(Manifest.permission.ACCESS_FINE_LOCATION)
    }

    private fun setGPSEnablePref(enabled: Boolean) {
        editor.putBoolean(Preferences.GPSEnable.value, enabled)
        editor.apply()
    }

    fun onPermissionResults(permissions: Array<out String>, grantResults: IntArray) {
        (0 until permissions.size)
                .filter { permissions[it] == Manifest.permission.ACCESS_FINE_LOCATION }
                .forEach {
                    setGPSEnablePref(grantResults[it] == PackageManager.PERMISSION_GRANTED)
                }
    }

    interface PermissionAcquirer {
        fun askForPermission(permission: String)
    }
}