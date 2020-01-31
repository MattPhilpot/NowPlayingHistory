package com.philpot.nowplayinghistory.lastfm

import com.philpot.nowplayinghistory.lastfm.api.http.JsonOperation
import org.json.JSONObject


/**
 * Created by MattPhilpot on 11/9/2017.
 */
class LfmRequest(
        /**
         * Selected method name
         */
        private val methodName: String,
        /**
         * Method parameters
         */
        private val parameters: LfmParameters,
        /**
         * Used for indicating if request need authentication.
         */
        private val needAuth: Boolean) {

    /**
     * Specify listener for current request
     */
    private var requestListener: LfmRequestListener? = null

    /**
     * Create new request with parameters.
     *
     * @param method     API - method name,e.g track.getInfo
     * @param parameters method parameters.
     */


    /**
     * Executes that request, and returns result to blocks
     *
     * @param listener listener for request events
     */
    fun executeWithListener(listener: LfmRequestListener) {
        this.requestListener = listener
        start()
    }

    private fun start() {
        JsonOperation(methodName, parameters).execute(requestListener)
    }

    abstract class LfmRequestListener {

        /**
         * Called if there were no HTTP or API errors, returns execution result.
         * @param response response from LfmRequest
         */
        abstract fun onComplete(response: JSONObject)


        /**
         * Called immediately if there was API error, or  if there was an HTTP error
         * @param error error for LfmRequest
         */
        abstract fun onError(error: LfmError)
    }
}