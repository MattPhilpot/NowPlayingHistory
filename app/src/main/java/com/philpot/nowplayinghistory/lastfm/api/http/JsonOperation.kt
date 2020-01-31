package com.philpot.nowplayinghistory.lastfm.api.http

import android.os.AsyncTask
import android.util.Log
import com.philpot.nowplayinghistory.lastfm.LfmError
import com.philpot.nowplayinghistory.lastfm.LfmParameters
import com.philpot.nowplayinghistory.lastfm.LfmRequest
import com.philpot.nowplayinghistory.lastfm.util.LfmUtil
import org.json.JSONException
import org.json.JSONObject

import java.io.BufferedReader
import java.io.DataOutputStream
import java.io.IOException
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL


/**
 * Created by MattPhilpot on 11/9/2017.
 */
class JsonOperation(private var method: String, private val lfmParameters: LfmParameters) : AsyncTask<LfmRequest.LfmRequestListener, Void, Void>() {

    companion object {
        private val ROOT_URL = "https://ws.audioscrobbler.com/2.0/"
        private val TAG = JsonOperation::class.java.simpleName
        private val POST = "POST"
    }

    //API request URL.
    private var requestURL: String? = null

    //Error from API request.
    private var error: LfmError = LfmError()

    //JSON response from API request.
    private var response: JSONObject? = null

    private var listener: LfmRequest.LfmRequestListener? = null

    override fun onPostExecute(result: Void?) {
        super.onPostExecute(result)
        response?.let {
            listener?.onComplete(it)
            return
        }
        listener?.onError(error)
    }

    override fun doInBackground(vararg params: LfmRequest.LfmRequestListener): Void? {
        this.listener = params[0]
        var connection: HttpURLConnection? = null
        try {
            connection = URL(LfmUtil.generateRequestURL(method, lfmParameters)).openConnection() as HttpURLConnection
            doJsonResponse(connection)
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            connection?.disconnect()
        }
        return null
    }

    /*
    @Throws(JSONException::class, IOException::class)
    private fun doJsonResponse(connection: HttpURLConnection) {
        handleResponse(connection)
    }
    */

    @Throws(JSONException::class, IOException::class)
    private fun doJsonResponse(connection: HttpURLConnection) {
        /*
        connection.requestMethod = POST
        connection.doOutput = true

        val dataOutStream = DataOutputStream(connection.outputStream)
        dataOutStream.writeBytes(LfmUtil.parseRestRequestParams(method, lfmParameters))
        dataOutStream.flush()
        dataOutStream.close()
        */
        handleResponse(connection)
    }

    private fun handleResponse(connection: HttpURLConnection) {
        if (connection.responseCode == 200) {
            handleBufferedReader(BufferedReader(InputStreamReader(connection.inputStream)))
        } else {
            error.errorCode = connection.responseCode
            error.errorMessage = connection.responseMessage
            response = null
        }
    }

    private fun handleBufferedReader(reader: BufferedReader) {
        try {
            val buffer = StringBuffer()

            var line = reader.readLine()
            while (line?.isNotBlank() == true) {
                buffer.append(line)
                line = reader.readLine()
            }

            response = JSONObject(buffer.toString())
            response?.let {
                if (response?.optString("error") != "") {
                    error = LfmError(it)
                    response = null
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, e.message, e)
        } finally {
            try {
                reader.close()
            } catch (e: IOException) {
                e.printStackTrace()
                error.httpClientError = true
                error.errorMessage = e.message
            }
        }
    }
}
