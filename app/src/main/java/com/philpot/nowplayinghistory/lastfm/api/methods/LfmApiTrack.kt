package com.philpot.nowplayinghistory.lastfm.api.methods

import com.philpot.nowplayinghistory.lastfm.LfmParameters
import com.philpot.nowplayinghistory.lastfm.LfmRequest
import com.philpot.nowplayinghistory.lastfm.util.LfmUtil
import java.util.*


/**
 * Created by MattPhilpot on 11/9/2017.
 */
class LfmApiTrack : LfmApiBase() {

    override val methodsGroup: String
        get() = "track"

    /**
     * http://www.last.fm/api/show/track.search
     */
    fun search(params: LfmParameters): LfmRequest {
        return prepareRequest("search", params, false)
    }

    /**
     * http://www.last.fm/api/show/track.getInfo
     */
    fun getInfo(params: LfmParameters): LfmRequest {
        return prepareRequest("getInfo", params, false)
    }
}