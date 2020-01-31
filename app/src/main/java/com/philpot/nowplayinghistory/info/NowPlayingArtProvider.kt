package com.philpot.nowplayinghistory.info

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import com.philpot.nowplayinghistory.BuildConfig
import com.philpot.nowplayinghistory.db.dao.AlbumInfoDao
import com.philpot.nowplayinghistory.db.dao.SongInfoDao
import com.philpot.nowplayinghistory.lastfm.LfmError
import com.philpot.nowplayinghistory.lastfm.LfmParameters
import com.philpot.nowplayinghistory.lastfm.LfmRequest
import com.philpot.nowplayinghistory.lastfm.api.LfmApi
import com.philpot.nowplayinghistory.model.AlbumInfo
import com.philpot.nowplayinghistory.model.HistoryItem
import com.philpot.nowplayinghistory.model.SongInfo
import org.json.JSONArray
import org.json.JSONObject
import java.io.File

import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.async
import java.io.FileOutputStream
import java.io.IOException
import java.net.URL
import java.util.*

/**
 * Created by MattPhilpot on 11/8/2017.
 */
class NowPlayingArtProvider(private val songInfoDao: SongInfoDao,
                            private val albumInfoDao: AlbumInfoDao,
                            private var cacheDirectory: String) : AlbumArtCacheProvider  {

    companion object {
        private val TAG = NowPlayingArtProvider::class.java.simpleName
    }

    override fun getAllAlbumArtAsync() {
        async(CommonPool) {
            songInfoDao.getAll()
                    .asSequence()
                    .filter { attemptCacheFetch(it) == null }
                    .forEach { getAlbumInfo(it, null) }
        }
    }

    override fun getAlbumInfoAsync(item: HistoryItem, callback: AlbumArtCacheProvider.AlbumArtCallback?) {
        async(CommonPool) {
            songInfoDao.getSongInfoFrom(item).let {
                getAlbumInfoAsync(it, callback)
            }
        }
    }

    override fun getAlbumInfoAsync(songInfo: SongInfo, callback: AlbumArtCacheProvider.AlbumArtCallback?) {
        async(CommonPool) {
            attemptCacheFetch(songInfo)?.let {
                callback?.onAlbumArtLoaded(it, songInfo)
                return@async
            }

            getAlbumInfo(songInfo, callback)
        }
    }

    override fun attemptCacheFetch(item: HistoryItem): Bitmap? {
        return attemptCacheFetch(songInfoDao.getSongInfoFrom(item))
    }

    override fun attemptCacheFetch(songInfo: SongInfo): Bitmap? {
        if (songInfo.albumInfo == null) {
            songInfo.albumInfo = albumInfoDao.getAlbumInfoFrom(songInfo)
        }
        return attemptCacheFetch(songInfo.albumInfo)
    }

    override fun attemptCacheFetch(albumInfo: AlbumInfo?): Bitmap? {
        albumInfo?.let { album ->
            try {
                album.albumArtPath?.let {
                    if (it.isNotBlank()) {
                        val options = BitmapFactory.Options()
                        options.inPreferredConfig = Bitmap.Config.ARGB_8888
                        BitmapFactory.decodeFile("$cacheDirectory/$it", options)?.let {
                            return it
                        }
                    }
                }
            } catch (e: IOException) {
                album.albumArtPath = ""
                albumInfoDao.insertOrUpdate(album)
            }
        }
        return null
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

    private fun getAlbumInfo(item: SongInfo, callback: AlbumArtCacheProvider.AlbumArtCallback?, trimTitle: Boolean = false) {
        val params = LfmParameters()
        params.put("artist", item.artist)
        params.put("track", getSearchTitle(item.title, trimTitle))

        val request = LfmApi.track().getInfo(params)
        request.executeWithListener(object : LfmRequest.LfmRequestListener() {
            override fun onComplete(response: JSONObject) {
                getAlbumInfoFrom(item, response, params, callback, trimTitle)
            }

            override fun onError(error: LfmError) {
                if (BuildConfig.DEBUG) {
                    Log.i(TAG, error.errorMessage ?: "no error")
                }
                if (!trimTitle) {
                    getAlbumInfo(item, callback, true)
                }
            }
        })
    }

    private fun getAlbumInfoFrom(songInfo: SongInfo,
                                 trackResponse: JSONObject,
                                 params: LfmParameters,
                                 callback: AlbumArtCacheProvider.AlbumArtCallback?,
                                 repeatAttempt: Boolean) {
        try {
            val albumTitle = (((trackResponse["track"] as JSONObject).get("album")) as JSONObject).get("title").toString()
            songInfo.album = albumTitle
            songInfoDao.insertOrUpdate(songInfo)
            songInfo.albumInfo = albumInfoDao.getAlbumInfoFrom(songInfo)

            songInfo.albumInfo?.let { albumInfo ->
                params.put("album", albumTitle)

                val request = LfmApi.album().getInfo(params)
                request.executeWithListener(object : LfmRequest.LfmRequestListener() {
                    override fun onComplete(response: JSONObject) {
                        asyncLoadAlbumArt(songInfo, albumInfo, (((response.get("album") as JSONObject).get("image") as JSONArray)[1] as JSONObject).get("#text").toString(), callback)
                    }

                    override fun onError(error: LfmError) {
                        if (BuildConfig.DEBUG) {
                            Log.i(TAG, error.errorMessage ?: "no error")
                        }
                    }
                })
            }

        } catch (e: Exception) {
            if (!repeatAttempt) {
                getAlbumInfo(songInfo, callback, true)
            }
        }
    }

    private fun asyncLoadAlbumArt(songInfo: SongInfo,
                                  albumInfo: AlbumInfo,
                                  url: String?, callback: AlbumArtCacheProvider.AlbumArtCallback?) {
        url?.let {
            if (it.isNotBlank()) {
                async(CommonPool) {
                    try {
                        val inValue = URL(it)
                        val albumBitmap = BitmapFactory.decodeStream(inValue.openConnection()?.getInputStream())
                        saveBitmapToCache(albumInfo, albumBitmap)
                        songInfo.albumInfo = albumInfo
                        callback?.onAlbumArtLoaded(albumBitmap, songInfo)

                    } catch (e: Exception) {
                        Log.e(TAG, "Failed to load image", e)
                    }
                }
            }
        }
    }

    private fun saveBitmapToCache(item: AlbumInfo, bitmap: Bitmap) {
        var saveName = UUID.randomUUID().toString() + ".png"
        var file = File(cacheDirectory, saveName)
        while (file.exists()) {
            saveName = UUID.randomUUID().toString() + ".png"
            file = File(cacheDirectory, saveName)
        }

        try {
            val out = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, out)
            out.flush()
            out.close()

            item.albumArtPath = saveName
            albumInfoDao.insertOrUpdate(item)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}