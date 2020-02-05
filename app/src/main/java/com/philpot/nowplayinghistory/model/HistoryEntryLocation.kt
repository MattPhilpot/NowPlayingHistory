package com.philpot.nowplayinghistory.model

import androidx.room.Entity

/**
 * Created by MattPhilpot on 11/14/2017.
 */
data class HistoryEntryLocation(val latitude: Double,
                                val longitude: Double,
                                val accuracy: Float)