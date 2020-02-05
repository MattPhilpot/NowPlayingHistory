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
import com.philpot.nowplayinghistory.model.Album
import com.philpot.nowplayinghistory.model.HistoryEntry
import com.philpot.nowplayinghistory.model.Song
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

    override fun getAlbumInfoAsync(entry: HistoryEntry, callback: AlbumArtCacheProvider.AlbumArtCallback?) {
        async(CommonPool) {
            songInfoDao.getSongInfoFrom(entry).let {
                getAlbumInfoAsync(it, callback)
            }
        }
    }

    override fun getAlbumInfoAsync(song: Song, callback: AlbumArtCacheProvider.AlbumArtCallback?) {
        async(CommonPool) {
            attemptCacheFetch(song)?.let {
                callback?.onAlbumArtLoaded(it, song)
                return@async
            }

            getAlbumInfo(song, callback)
        }
    }

    override fun attemptCacheFetch(entry: HistoryEntry): Bitmap? {
        return attemptCacheFetch(songInfoDao.getSongInfoFrom(entry))
    }

    override fun attemptCacheFetch(song: Song): Bitmap? {
        if (song.album == null) {
            song.album = albumInfoDao.getAlbumInfoFrom(song)
        }
        return attemptCacheFetch(song.album)
    }

    override fun attemptCacheFetch(album: Album?): Bitmap? {
        album?.let { album ->
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

    private fun getAlbumInfo(item: Song, callback: AlbumArtCacheProvider.AlbumArtCallback?, trimTitle: Boolean = false) {
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

    private fun getAlbumInfoFrom(song: Song,
                                 trackResponse: JSONObject,
                                 params: LfmParameters,
                                 callback: AlbumArtCacheProvider.AlbumArtCallback?,
                                 repeatAttempt: Boolean) {
        try {
            val albumTitle = (((trackResponse["track"] as JSONObject).get("album")) as JSONObject).get("title").toString()
            song.album = albumTitle
            songInfoDao.insertOrUpdate(song)
            song.album = albumInfoDao.getAlbumInfoFrom(song)

            song.album?.let { albumInfo ->
                params.put("album", albumTitle)

                val request = LfmApi.album().getInfo(params)
                request.executeWithListener(object : LfmRequest.LfmRequestListener() {
                    override fun onComplete(response: JSONObject) {
                        asyncLoadAlbumArt(song, albumInfo, (((response.get("album") as JSONObject).get("image") as JSONArray)[1] as JSONObject).get("#text").toString(), callback)
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
                getAlbumInfo(song, callback, true)
            }
        }
    }

    private fun asyncLoadAlbumArt(song: Song,
                                  album: Album,
                                  url: String?, callback: AlbumArtCacheProvider.AlbumArtCallback?) {
        url?.let {
            if (it.isNotBlank()) {
                async(CommonPool) {
                    try {
                        val inValue = URL(it)
                        val albumBitmap = BitmapFactory.decodeStream(inValue.openConnection()?.getInputStream())
                        saveBitmapToCache(album, albumBitmap)
                        song.album = album
                        callback?.onAlbumArtLoaded(albumBitmap, song)

                    } catch (e: Exception) {
                        Log.e(TAG, "Failed to load image", e)
                    }
                }
            }
        }
    }

    private fun saveBitmapToCache(item: Album, bitmap: Bitmap) {
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