package com.philpot.nowplayinghistory.lastfm.util

import com.philpot.nowplayinghistory.lastfm.Lfm
import com.philpot.nowplayinghistory.lastfm.LfmParameters
import com.philpot.nowplayinghistory.lastfm.LfmSession
import java.io.UnsupportedEncodingException
import java.math.BigInteger
import java.net.URLEncoder
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import java.util.ArrayList
import java.util.Collections


/**
 * Created by MattPhilpot on 11/9/2017.
 */
object LfmUtil {

    /**
     * The API root URL.
     */
    private val ROOT_URL = "http://ws.audioscrobbler.com/2.0/"

    /**
     * Making string from LfmParameters.
     */
    fun paramsParser(params: LfmParameters): String {
        val builder = StringBuilder()
        var p: String
        for (key in params.keys) {
            try {
                p = key + "=" + URLEncoder.encode(params[key], "UTF-8")
                builder.append("&").append(p)
            } catch (e: UnsupportedEncodingException) {
                e.printStackTrace()
            }

        }
        return builder.toString()
    }


    private fun methodParser(method: String): String {
        return "?method=" + method
    }

    fun generateRequestURL(method: String, params: LfmParameters?): String {
        val builder = StringBuilder()
        builder.append(ROOT_URL)
                .append(methodParser(method))
                .append("&api_key=")
                .append(Lfm.apiKey)
                .append("&format=json")
        if (params != null)
            builder.append(paramsParser(params))
        return builder.toString()
    }

    /**
     * Method for generating signature.
     */
    fun generateSignature(method: String, params: LfmParameters): String {
        val parameters = ArrayList(params.keys)
        parameters.add("method" + method)
        parameters.add("sk")
        Collections.sort(parameters)
        val builder = StringBuilder()
        builder.append("api_key").append(Lfm.apiKey)
        for (p in parameters) {
            when (p) {
                "method" + method -> builder.append(p)
                "sk" -> builder.append("sk").append(LfmSession.sessionkey)
                else -> builder.append(p).append(params[p])
            }
        }
        builder.append(Lfm.secret)
        return LfmUtil.md5Custom(builder.toString())
    }

    /**
     * Method for parsing parameters for REST request.
     */
    fun parseRestRequestParams(method: String, params: LfmParameters): String {
        val builder = StringBuilder()
        builder.append(methodParser(method).substring(1))
                .append(paramsParser(params))
                .append("&api_key=")
                .append(Lfm.apiKey)
                .append("&sk=")
                .append(LfmSession.sessionkey)
                .append("&format=json")
        return builder.toString()
    }


    /**
     * Method for generating MD5 hash.
     */
    fun md5Custom(st: String): String {
        val messageDigest: MessageDigest
        var digest = ByteArray(0)

        try {
            messageDigest = MessageDigest.getInstance("MD5")
            messageDigest.reset()
            messageDigest.update(st.toByteArray())
            digest = messageDigest.digest()
        } catch (e: NoSuchAlgorithmException) {
            e.printStackTrace()
        }

        val bigInt = BigInteger(1, digest)
        var md5Hex = bigInt.toString(16)

        while (md5Hex.length < 32) {
            md5Hex = "0" + md5Hex
        }

        return md5Hex
    }
}