package com.philpot.nowplayinghistory.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import org.kodein.di.android.closestKodein

/**
 * Created by MattPhilpot on 12/1/2017.
 */
abstract class NowPlayingFragment : Fragment(), KodeinAware {

    protected val parentKodein: Kodein by lazy {
        closestKodein().provideDelegate(this@NowPlayingFragment.context!!, ::parentKodein).value
    }

    init {
        if (arguments == null) {
            arguments = Bundle()
        }
    }
}
