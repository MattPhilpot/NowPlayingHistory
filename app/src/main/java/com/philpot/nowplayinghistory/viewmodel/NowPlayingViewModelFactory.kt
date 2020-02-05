package com.philpot.nowplayinghistory.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModelProvider
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import org.kodein.di.android.closestKodein

class NowPlayingViewModelFactory(context: Context) : ViewModelProvider.NewInstanceFactory(), KodeinAware {

    private val parentKodein by closestKodein(context)

    override val kodein = Kodein.lazy {
        extend(parentKodein)
    }
}
