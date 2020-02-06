package com.philpot.nowplayinghistory.settings

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Switch
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.switchmaterial.SwitchMaterial
import com.philpot.nowplayinghistory.R
import com.philpot.nowplayinghistory.coroutine.CoroutineContextProvider
import com.philpot.nowplayinghistory.db2.NowPlayingDatabase
import com.philpot.nowplayinghistory.db2.manager.ParameterManager
import com.philpot.nowplayinghistory.model.MusicAppPreference
import com.philpot.nowplayinghistory.model.param.BooleanParameter
import com.philpot.nowplayinghistory.model.param.ParameterType
import com.philpot.nowplayinghistory.util.TestRecordInsertUtil
import kotlinx.android.synthetic.main.layout_settings.*
import kotlinx.android.synthetic.main.layout_settings.layout_settings_add_test_records
import kotlinx.android.synthetic.main.layout_settings.layout_settings_get_gps
import kotlinx.android.synthetic.main.layout_settings.layout_settings_lastfm_integration
import kotlinx.android.synthetic.main.layout_settings.layout_settings_play_song_spinner
import kotlinx.android.synthetic.main.layout_settings.layout_settings_scroll_to_top
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import org.kodein.di.android.closestKodein

/**
 * Created by MattPhilpot on 11/1/2017.
 */
class SettingsBottomSheetDialog(context: Context,
                                private val permissionAcquirer: PermissionAcquirer,
                                private val parameterManager: ParameterManager,
                                private val coroutineContextProvider: CoroutineContextProvider) : BottomSheetDialog(context) {

    init {
        setContentView(R.layout.layout_settings)
        setValuesFromPreferences()
    }

    fun setValuesFromPreferences() {

        coroutineContextProvider.mainScope.launch {
            val spinnerArrayAdapter = ArrayAdapter(context, android.R.layout.simple_spinner_item, MusicAppPreference.getAvailableList(context))
            spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            layout_settings_play_song_spinner?.adapter = spinnerArrayAdapter
            layout_settings_play_song_spinner?.setSelection(0)//preferences.getInt(Preferences.CurrentMusicApp.value, 0))
            layout_settings_play_song_spinner?.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onNothingSelected(p0: AdapterView<*>?) {
                    //do nothing
                }

                override fun onItemSelected(p0: AdapterView<*>?, view: View?, position: Int, id: Long) {
                    //eventBus.post(SettingsChangedEvent(Preferences.CurrentMusicApp))
                    /*
                    editor.putInt(Preferences.CurrentMusicApp.value, position)
                    editor.apply()
                    */
                    //val kodein = closestKodein(context)
                    //ShortcutHelper.updateShortcuts(context, kodein.instance(), kodein.instance())
                }
            }

            layout_settings_add_test_records?.setOnClickListener {
                coroutineContextProvider.ioScope.launch {
                    TestRecordInsertUtil.insertTestRecords(NowPlayingDatabase.getInstance(context))
                }
            }

            layout_settings_scroll_to_top?.isChecked = getParameter(ParameterType.ScrollToTop).await()
            layout_settings_scroll_to_top?.setOnCheckedChangeListener { _, b ->
                parameterManager.ScrollToTop = b
            }
            doGPSEnableListenerSetup()
            doLastFMCheckCangeListenerSetup()


        }
    }

    override fun onAttachedToWindow() {
        applyLayoutParams()
        super.onAttachedToWindow()
    }

    private fun applyLayoutParams() {
        window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        linear_layout_bottom_sheet?.parent?.let {
            BottomSheetBehavior.from(it as View).apply {
                peekHeight = context.resources.displayMetrics.heightPixels
                state = BottomSheetBehavior.STATE_EXPANDED
            }
        }
    }

    private fun getParameter(parameterType: ParameterType): Deferred<Boolean> = coroutineContextProvider.ioScope.async {
        return@async parameterManager.getBooleanParameter(parameterType).value
    }

    private suspend fun doGPSEnableListenerSetup() {
        layout_settings_get_gps.isChecked = getParameter(ParameterType.GPSEnable).await()
        layout_settings_get_gps?.setOnCheckedChangeListener { _, b ->
            if (b) {
                doGPSPermissionCheck()
            } else {
                setGPSEnablePref(b)
            }
        }
    }

    private suspend fun doLastFMCheckCangeListenerSetup() {
        layout_settings_lastfm_integration.isChecked = getParameter(ParameterType.LastFmIntegration).await()
        layout_settings_lastfm_integration.setOnCheckedChangeListener { _, b ->
            //eventBus.post(SettingsChangedEvent(Preferences.LastFmIntegration))
            parameterManager.LastFmIntegration = b
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
        parameterManager.GPSEnable = enabled
    }

    fun onPermissionResults(permissions: Array<out String>, grantResults: IntArray) {
        /*
        (permissions.indices)
                .filter { permissions[it] == Manifest.permission.ACCESS_FINE_LOCATION }
                .forEach { setGPSEnablePref(grantResults[it] == PackageManager.PERMISSION_GRANTED) }
        */
    }

    interface PermissionAcquirer {
        fun askForPermission(permission: String)
    }
}