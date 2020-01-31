package com.philpot.nowplayinghistory.fragment

import android.content.Context
import android.os.Bundle
import android.view.View
import com.github.salomonbrys.kodein.android.KodeinFragment
import com.philpot.nowplayinghistory.activity.NowPlayingContainer

/**
 * Created by MattPhilpot on 12/1/2017.
 */
abstract class NowPlayingFragment : KodeinFragment() {

    abstract val fragmentTitle: Int
    abstract val fragmentTag: String

    protected fun getNowPlayingContainer(): NowPlayingContainer? {
        activity?.let {
            if (it is NowPlayingContainer) {
                return it
            }
        }
        return null
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        activity?.setTitle(fragmentTitle)
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        //initializeInjector()
        //injector.inject(appKodein())
    }

    override fun onResume() {
        super.onResume()
        activity.invalidateOptionsMenu()
    }

    companion object {
        private val FIRST_LOAD = "FIRST_LOAD"
    }
}