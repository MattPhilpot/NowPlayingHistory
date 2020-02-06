package com.philpot.nowplayinghistory.listener

import android.Manifest
import android.app.DownloadManager
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import android.content.Intent
import android.os.IBinder
import android.content.Context
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.location.Location
import android.location.LocationListener
import android.util.Log
import com.philpot.nowplayinghistory.model.HistoryEntry
import com.philpot.nowplayinghistory.model.SongInfo
import com.philpot.nowplayinghistory.util.ShortcutHelper
import android.location.LocationManager
import android.os.Bundle
import androidx.core.content.ContextCompat
import com.philpot.nowplayinghistory.BuildConfig
import com.philpot.nowplayinghistory.R
import com.philpot.nowplayinghistory.db2.NowPlayingDatabase
import com.philpot.nowplayinghistory.db2.dao.HistoryDao
import com.philpot.nowplayinghistory.db2.dao.ParameterDao
import com.philpot.nowplayinghistory.db2.dao.SongInfoDao
import com.philpot.nowplayinghistory.lastfm.LfmError
import com.philpot.nowplayinghistory.lastfm.LfmParameters
import com.philpot.nowplayinghistory.lastfm.LfmRequest
import com.philpot.nowplayinghistory.lastfm.api.LfmApi
import com.philpot.nowplayinghistory.model.AlbumInfo
import com.philpot.nowplayinghistory.model.ArtistInfo
import com.philpot.nowplayinghistory.model.HistoryEntryLocation
import com.philpot.nowplayinghistory.model.param.ParameterType
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import org.joda.time.DateTime
import org.json.JSONArray
import org.json.JSONObject


/**
 * Created by colse on 10/29/2017.
 */
class NowPlayingListener : NotificationListenerService() {

    private val database by lazy {
        NowPlayingDatabase.getInstance(applicationContext)
    }

    override fun onBind(mIntent: Intent): IBinder? {
        val mIBinder = super.onBind(mIntent)
        logInfo("onBind")
        return mIBinder
    }

    override fun onUnbind(mIntent: Intent): Boolean {
        val mOnUnbind = super.onUnbind(mIntent)
        logInfo("onUnbind")
        return mOnUnbind
    }

    override fun onListenerConnected() {
        super.onListenerConnected()
        logInfo("Listener Connected")
    }

    override fun onListenerDisconnected() {
        super.onListenerDisconnected()
        logInfo("Listener Disconnected")
    }

    override fun onNotificationPosted(sbn: StatusBarNotification?) {
        sbn?.let {
            if ("com.google.intelligence.sense" == it.packageName) {
                try {
                    val entry = it.notification.extras["android.title"].toString()
                    logInfo(entry)
                    saveNewEntry(entry)
                } catch (e: Exception) {
                    logError("Error saving entry", e)
                }
            }
        }
    }

    override fun onNotificationRemoved(sbn: StatusBarNotification?) {
        /*
        if (sbn.packageName == "com.google.intelligence.sense") {
            logInfo("Now Playing Removed")
        }
        */
    }

    private fun saveNewEntry(entry: String) {
        CoroutineScope(Dispatchers.IO).launch {
            val split = entry.split(" by ")

            if (split.size < 2) {
                return@launch
            }

            var title = ""
            val artist = split.last()

            for (i in 0..split.size - 2) {
                if (title.isNotBlank()) {
                    title += " by "
                }
                title += split[i]
            }

            if (title.isBlank() || artist.isBlank()) {
                return@launch
            }

            val artistDao = database.artistDao()
            val songDao = database.songDao()
            val historyDao = database.historyDao()

            //insert artist if it doesn't exist
            val artistInfo = ArtistInfo(name = artist, info = "")
            artistDao.insertOrUpdate(artistInfo)

            //insert song if it doesn't exist
            val songInfo = SongInfo(
                title = title,
                artist = artist)
            val songInfoId = songDao.insertOrUpdate(songInfo)

            val toAdd = HistoryEntry(timestamp = DateTime.now(), songId = songInfoId, artist = artist)
            historyDao.insertIfNotRepeat(toAdd)?.let { id ->
                toAdd.id = id
                doHistoryItemUpdate(toAdd, historyDao, songInfo, songDao)
            }
        }
    }

    private fun doHistoryItemUpdate(entry: HistoryEntry, historyDao: HistoryDao, songInfo: SongInfo, songInfoDao: SongInfoDao) {
        try {
            //songInfoDao.updateLastHeard(entry) //don't need anymore I think
            val parameterDao = database.parameterDao()
            doGPSIfAble(entry, historyDao, parameterDao)
            getAlbumArtForShortcuts(parameterDao, songInfo, songInfoDao, historyDao)
        } catch (e : Exception) {
            // do nothing, just don't crash
        } finally {
            //EventBus.getDefault().post(NewHistoryItemEvent(entry))
        }
    }

    private fun doGPSIfAble(entry: HistoryEntry, historyDao: HistoryDao, parameterDao: ParameterDao) {
        val gpsEnabled = parameterDao.getById(ParameterType.GPSEnable.code)?.value?.toBoolean() ?: false
        if (gpsEnabled && ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            val locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
            locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)?.let {
                if (it.hasAccuracy() && entry.timestamp.millis - it.time < 300000) {
                    saveHistoryItemWithLocation(it, entry, historyDao)
                    return
                }
            }

            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000L, 0F, object : LocationListener {
                override fun onLocationChanged(location: Location?) {
                    location?.let {
                        if (it.hasAccuracy()) {
                            saveHistoryItemWithLocation(it, entry, historyDao)
                            locationManager.removeUpdates(this)
                            return
                        }
                    }
                }
                override fun onStatusChanged(p0: String?, p1: Int, p2: Bundle?) { /* do nothing */ }
                override fun onProviderEnabled(p0: String?) { /* do nothing */ }
                override fun onProviderDisabled(p0: String?) { /* do nothing */ }
            })
        }
    }

    private fun saveHistoryItemWithLocation(location: Location, entry: HistoryEntry, historyDao: HistoryDao) {
        entry.location = HistoryEntryLocation(longitude = location.longitude, latitude = location.latitude, accuracy = location.accuracy)
        historyDao.insertOrUpdate(entry)
    }


    private fun getAlbumArtForShortcuts(parameterDao: ParameterDao, songInfo: SongInfo, songInfoDao: SongInfoDao, historyDao: HistoryDao) {
        try {
            val lastFMEnabled = parameterDao.getById(ParameterType.LastFmIntegration.code)?.value?.toBoolean() ?: false
            if (lastFMEnabled) {

                getAlbumInfo(songInfo, false)
            }
        } catch (e : Exception) {
            ShortcutHelper.updateShortcuts(applicationContext, historyDao, songInfoDao)
        }
    }

    private fun logError(message: String, error: Exception) {
        Log.e(TAG, message, error)
    }

    private fun logInfo(message: String) {
        if (BuildConfig.DEBUG) {
            Log.i(TAG, message)
        }
    }

    private fun getAlbumInfo(item: SongInfo, trimTitle: Boolean = false) {
        val params = LfmParameters()
        params["artist"] = item.artist
        params["track"] = getSearchTitle(item.title, trimTitle)

        val request = LfmApi.track().getInfo(params)
        request.executeWithListener(object : LfmRequest.LfmRequestListener() {
            override fun onComplete(response: JSONObject) {
                getAlbumInfoFrom(item, response, params, trimTitle)
            }

            override fun onError(error: LfmError) {
                /*
                if (BuildConfig.DEBUG) {
                    Log.i(TAG, error.errorMessage ?: "no error")
                }
                */
                if (!trimTitle) {
                    getAlbumInfo(item, true)
                }
            }
        })
    }

    private fun getAlbumInfoFrom(songInfo: SongInfo,
                                 trackResponse: JSONObject,
                                 params: LfmParameters,
                                 repeatAttempt: Boolean) {
        try {
            val albumTitle = (((trackResponse["track"] as JSONObject).get("album")) as JSONObject).get("title").toString()
            val albumDao = database.albumDao()
            val currentAlbum = albumDao.getByArtistAndTitle(songInfo.artist, albumTitle)
                ?: AlbumInfo(title = albumTitle, artist = songInfo.artist, year = null, albumArtPath = null)
            currentAlbum.id = albumDao.insertOrUpdate(currentAlbum)
            songInfo.albumId = currentAlbum.id
            database.songDao().insertOrUpdate(songInfo)
            //songInfo.album = albumInfoDao.getAlbumInfoFrom(songInfo)


            params["album"] = albumTitle
            val request = LfmApi.album().getInfo(params)
            request.executeWithListener(object : LfmRequest.LfmRequestListener() {
                override fun onComplete(response: JSONObject) {
                    currentAlbum.albumArtPath = (((response.get("album") as JSONObject).get("image") as JSONArray)[1] as JSONObject).get("#text").toString()
                    albumDao.update(currentAlbum)
                }

                override fun onError(error: LfmError) {
                    /*
                    if (BuildConfig.DEBUG) {

                    }
                    */
                    Log.i(TAG, error.errorMessage ?: "no error")
                }
            })

        } catch (e: Exception) {
            if (!repeatAttempt) {
                getAlbumInfo(songInfo, true)
            }
        }
    }

    private fun getSearchTitle(title: String, trimTitle: Boolean): String {
        if (!trimTitle) {
            return title
        }

        var retVal = title
        if (retVal.contains("(")) {
            retVal = title.substring(0, title.indexOf("("))
        }

        if (retVal.contains("[")) {
            retVal = title.substring(0, title.indexOf("["))
        }
        return retVal
    }


    companion object {
        private val TAG = NowPlayingListener::class.java.simpleName
    }
}