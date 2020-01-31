package com.philpot.nowplayinghistory.lastfm.api

import com.philpot.nowplayinghistory.lastfm.api.methods.LfmApiAlbum
import com.philpot.nowplayinghistory.lastfm.api.methods.LfmApiTrack

/**
 * Created by MattPhilpot on 11/9/2017.
 */
object LfmApi {

    /**
     * Returns object for preparing requests to track part of API
     */
    fun track(): LfmApiTrack {
        return LfmApiTrack()
    }

    /*
     * Returns object for preparing requests to album part of API
     */
    fun album(): LfmApiAlbum {
        return LfmApiAlbum()
    }
}