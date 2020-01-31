package com.philpot.nowplayinghistory.lastfm.api.methods

import com.philpot.nowplayinghistory.lastfm.LfmParameters
import com.philpot.nowplayinghistory.lastfm.LfmRequest
import com.philpot.nowplayinghistory.lastfm.util.LfmUtil
import java.util.*


/**
 * Created by MattPhilpot on 11/9/2017.
 */
class LfmApiAlbum : LfmApiBase() {

    override val methodsGroup: String
        get() = "album"

    //http://www.last.fm/api/show/album.getInfo
    fun getInfo(params: LfmParameters): LfmRequest {
        return prepareRequest("getInfo", params, false)
    }

    //http://www.last.fm/api/show/album.search
    fun search(params: LfmParameters): LfmRequest {
        return prepareRequest("search", params, false)
    }
}