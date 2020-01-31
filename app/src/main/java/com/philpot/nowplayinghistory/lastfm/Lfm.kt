package com.philpot.nowplayinghistory.lastfm

import android.app.Application

import com.philpot.nowplayinghistory.R


/**
 * Created by MattPhilpot on 11/9/2017.
 */
object Lfm {

    lateinit var apiKey: String
        private set

    lateinit var secret: String
        private set

    /**
     * Method for API key initialization.
     */
    fun initialize(context: Application) {
        Lfm.apiKey = context.getString(R.string.lfm_api_key)
        Lfm.secret = context.getString(R.string.lfm_secret)
    }
    /**
     * Authorization callback.
     */
    abstract class LfmCallback<in T> {

        /**
         * Called if user successfully logged in.
         */
        abstract fun onResult(result: T)

        /**
         * Called immediately if there was authorization error, or  if there was an HTTP error.
         *
         * @param error error for LfmCallback
         */
        abstract fun onError(error: LfmError)

    }

}