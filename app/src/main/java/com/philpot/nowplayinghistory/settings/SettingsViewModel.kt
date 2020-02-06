package com.philpot.nowplayinghistory.settings

import android.util.Log
import androidx.databinding.ObservableBoolean
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.philpot.nowplayinghistory.coroutine.CoroutineContextProvider
import com.philpot.nowplayinghistory.db2.dao.ParameterDao
import com.philpot.nowplayinghistory.db2.manager.ParameterManager
import com.philpot.nowplayinghistory.model.param.Parameter
import kotlinx.coroutines.launch

class SettingsViewModel(private val parameterManager: ParameterManager,
                        coroutineContextProvider: CoroutineContextProvider) : ViewModel() {

    init {
        coroutineContextProvider.ioScope.launch {
            scrollToTopObservable.set(parameterManager.ScrollToTop)
            //useLastFMObservable.set(parameterManager.LastFmIntegration)
            useGPSObservable.set(parameterManager.GPSEnable)
        }
    }
    private val scrollToTopObservable = ObservableBoolean(false)
    private val useLastFMObservable = ObservableBoolean(true)
    private val useGPSObservable = ObservableBoolean(false)

    val scrollToTop: Boolean
        get() = scrollToTopObservable.get()

    val useLastFM: Boolean
        get() = useLastFMObservable.get()

    val useGPS: Boolean
        get() = useGPSObservable.get()

    fun scrollToTopSelected(checked: Boolean) {
        Log.i("SettingsViewModel", "scrolltotopselected")
        val reverse = !scrollToTopObservable.get()
        scrollToTopObservable.set(reverse)
        parameterManager.ScrollToTop = reverse
    }
}