package com.philpot.nowplayinghistory.lastfm.api.methods

import com.philpot.nowplayinghistory.lastfm.LfmRequest
import com.philpot.nowplayinghistory.lastfm.LfmParameters
import java.util.*


/**
 * Created by MattPhilpot on 11/9/2017.
 */
abstract class LfmApiBase {

    /**
     * Selected methods group
     */
    protected abstract val methodsGroup: String

    protected fun prepareRequest(methodName: String, methodParameters: LfmParameters, needAuth: Boolean): LfmRequest {
        return LfmRequest(String.format(Locale.US, "%s.%s", methodsGroup, methodName), methodParameters, needAuth)
    }
}