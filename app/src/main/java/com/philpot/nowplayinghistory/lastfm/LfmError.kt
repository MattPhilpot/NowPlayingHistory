package com.philpot.nowplayinghistory.lastfm

import org.json.JSONException
import org.json.JSONObject
import java.util.*


/**
 * Created by MattPhilpot on 11/9/2017.
 */
class LfmError {


    /**
     * API error code/
     */
    var errorCode: Int = 0

    /**
     * Http Request error.
     */
    var httpClientError: Boolean = false

    /**
     * API error message.
     */
    var errorMessage: String? = null

    /**
     * API error reason.
     */
    var errorReason: String? = null

    constructor() {}

    @Throws(JSONException::class)
    constructor(json: JSONObject) {
        this.errorCode = json.optInt("error")
        if (json.optString("message").contains("-")) {
            this.errorReason = json.optString("message").substring(0, json.optString("message").lastIndexOf("-") - 1)
            this.errorMessage = json.optString("message").substring(json.optString("message").lastIndexOf("-") + 1)
        } else {
            this.errorMessage = json.optString("message")
        }
    }

    private fun appendFields(builder: StringBuilder) {
        if (errorReason != null)
            builder.append(String.format("; %s", errorReason))
        if ((errorMessage != null) and !httpClientError)
            builder.append(String.format("; %s", errorMessage))
        else
            builder.append(errorMessage)
    }

    override fun toString(): String {
        val errorString = StringBuilder("LfmError (")
        if (!httpClientError)
            errorString.append(String.format(Locale.US, "code: %d", this.errorCode))
        appendFields(errorString)
        return errorString.append(")").toString()
    }

}