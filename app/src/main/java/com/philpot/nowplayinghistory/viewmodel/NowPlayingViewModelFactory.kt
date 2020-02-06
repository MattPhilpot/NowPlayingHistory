package com.philpot.nowplayinghistory.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.philpot.nowplayinghistory.history2.History2ViewModel
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import org.kodein.di.android.closestKodein
import org.kodein.di.direct
import org.kodein.di.erased.instance

class NowPlayingViewModelFactory(context: Context) : ViewModelProvider.NewInstanceFactory(), KodeinAware {

    private val parentKodein by closestKodein(context)

    override val kodein = Kodein.lazy {
        extend(parentKodein)
    }

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        val direct = kodein.direct
        return when(modelClass) {
            History2ViewModel::class.java -> History2ViewModel(direct.instance()) as T
            else -> super.create(modelClass)
        }
    }
}
