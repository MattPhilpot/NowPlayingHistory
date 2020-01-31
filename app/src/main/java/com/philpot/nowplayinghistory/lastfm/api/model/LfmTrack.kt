package com.philpot.nowplayinghistory.lastfm.api.model

import android.os.Parcel
import android.os.Parcelable
import org.json.JSONObject



/**
 * Created by MattPhilpot on 11/9/2017.
 */
class LfmTrack() : android.os.Parcelable {

    /**
     * Artist name.
     */
    var artist: String? = null

    /**
     * Track playcount.
     */
    var playcount: Int = 0
    /**
     * The [musicbrainz](https://musicbrainz.org/) id for the track.
     */
    var mbid: String? = null

    /**
     * Track match.
     * See [track.getSimilar](http://www.last.fm/api/show/track.getSimilar)
     */
    var match: Int = 0

    /**
     * Track url.
     */
    var url: String? = null
    /**
     * Track duration (in milliseconds).
     */
    var duration: Int = 0

    /**
     * Track listeners number.
     */
    var listeners: Int = 0
    
    /**
     * Creates a LfmTrack instance from Parcel.
     */
    constructor(inParcel: Parcel) : this() {
        artist = inParcel.readString()
        mbid = inParcel.readString()
        url = inParcel.readString()
        duration = inParcel.readInt()
        listeners = inParcel.readInt()
        playcount = inParcel.readInt()

    }

    constructor(from: JSONObject) : this() {
        parse(from)
    }

    /**
     * Fills a LfmTrack instance from JSONObject.
     */
    private fun parse(from: JSONObject): LfmTrack {
        artist = from.optString("name")
        mbid = from.optString("mbid")
        url = from.optString("url")
        duration = setValueOf(from, "duration")
        listeners = setValueOf(from, "listeners")
        playcount = setValueOf(from, "playcount")
        return this
    }

    private fun setValueOf(from: JSONObject, value: String): Int {
        return if (from.optString(value) != "") {
            Integer.valueOf(from.optString(value))!!
        } else {
            from.optInt(value)
        }
    }

    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(dest: Parcel, flag: Int) {
        dest.writeString(artist)
        dest.writeString(mbid)
        dest.writeString(url)
        dest.writeInt(duration)
        dest.writeInt(listeners)
        dest.writeInt(playcount)
    }

    companion object {

        val CREATOR: Parcelable.Creator<LfmTrack> = object : Parcelable.Creator<LfmTrack> {
            override fun createFromParcel(`in`: Parcel): LfmTrack {
                return LfmTrack(`in`)
            }

            override fun newArray(size: Int): Array<LfmTrack?> {
                return arrayOfNulls(size)
            }
        }
    }
}