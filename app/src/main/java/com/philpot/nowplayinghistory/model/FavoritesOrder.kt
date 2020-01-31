package com.philpot.nowplayinghistory.model

import android.support.annotation.StringRes
import com.philpot.nowplayinghistory.R

/**
 * Created by MattPhilpot on 12/10/2017.
 */
enum class FavoritesOrder(@StringRes val stringResId: Int) {
    NEWEST_TO_OLDEST(R.string.favorites_order_newest_oldest),
    OLDEST_TO_NEWEST(R.string.favorites_order_oldest_newest),
    MOST_TIMES_HEARD(R.string.favorites_order_most_least),
    LEAST_TIMES_HEARD(R.string.favorites_order_least_most);

    companion object {
        fun getFrom(ordinal: Int): FavoritesOrder {
            values().forEach {
                if (it.ordinal == ordinal) {
                    return it
                }
            }
            return NEWEST_TO_OLDEST
        }
    }
}