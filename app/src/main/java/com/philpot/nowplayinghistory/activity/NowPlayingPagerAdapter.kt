package com.philpot.nowplayinghistory.activity

import android.app.Fragment
import android.app.FragmentManager
import android.support.v13.app.FragmentPagerAdapter
import com.philpot.nowplayinghistory.R
import com.philpot.nowplayinghistory.favorite.FavoriteFragment
import com.philpot.nowplayinghistory.history.HistoryFragment

/**
 * Created by MattPhilpot on 12/2/2017.
 */
class NowPlayingPagerAdapter(fragmentManager: FragmentManager) : FragmentPagerAdapter(fragmentManager) {

    private var internalHistory: HistoryFragment? = null
    private var internalFavorite: FavoriteFragment? = null

    private fun getHistoryFragment(): HistoryFragment {
        internalHistory?.let {
            return it
        }

        val retVal = HistoryFragment()
        internalHistory = retVal
        return retVal
    }

    private fun getFavoriteFragment(): FavoriteFragment {
        internalFavorite?.let {
            return it
        }

        val retVal = FavoriteFragment()
        internalFavorite = retVal
        return retVal
    }

    override fun getItem(position: Int): Fragment {
        return when(position) {
            0 -> getHistoryFragment()
            else -> getFavoriteFragment()
        }
    }

    override fun getCount(): Int {
        return 2
    }

    fun updateItemAt(position: Int) {
        when(position) {
            0 -> {
                getHistoryFragment().onControllerResume()
            }
            else -> {
                getFavoriteFragment().onControllerResume()
            }
        }
    }

    fun getTitle(position: Int): Int {
        return if (position == 0) {
            R.string.title_activity_main
        } else {
            R.string.title_activity_main_favorites
        }
    }
}