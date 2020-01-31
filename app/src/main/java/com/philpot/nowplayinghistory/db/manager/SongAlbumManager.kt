package com.philpot.nowplayinghistory.db.manager

import android.graphics.Bitmap
import com.philpot.nowplayinghistory.db.dao.AlbumInfoDao
import com.philpot.nowplayinghistory.db.dao.SongInfoDao
import com.philpot.nowplayinghistory.info.AlbumArtCacheProvider
import com.philpot.nowplayinghistory.model.AlbumInfo
import com.philpot.nowplayinghistory.model.HistoryItem
import com.philpot.nowplayinghistory.model.SongInfo

/**
 * Created by MattPhilpot on 11/9/2017.
 */
class SongAlbumManager(private val songInfoDao: SongInfoDao,
                       private val albumInfoDao: AlbumInfoDao,
                       private val albumArtProvider: AlbumArtCacheProvider) {

    companion object {
        private val songInfoMap = HashMap<String, SongInfo>()
        private val albumInfoMap = HashMap<String, AlbumInfo>()
    }

    var allowLastFM = false

    fun getSongInfoFor(item: HistoryItem): SongInfo {
        val key = SongInfoDao.getSongInfoKey(item)
        songInfoMap[key]?.let {
            if (it.albumInfo?.albumBitmap == null) {
                it.albumInfo = getAlbumInfoFor(it)
            }
            return it
        }

        songInfoDao.getSongInfoFrom(item).let {
            it.albumInfo = getAlbumInfoFor(it)

            songInfoMap.put(key, it)
            return it
        }
    }

    fun getAlbumInfoFor(songInfo: SongInfo): AlbumInfo? {
        songInfo.album?.let {
            val key = AlbumInfoDao.getAlbumKey(songInfo.artist, it)
            albumInfoMap[key]?.let {
                if (it.albumBitmap == null) {
                    it.albumBitmap = getAlbumBitmapFor(it)
                }

                if (it.albumBitmap == null) {
                    getAlbumInfoAsync(songInfo)
                }
                return it
            }

            albumInfoDao.getAlbumInfoFrom(songInfo)?.let {
                it.albumBitmap = getAlbumBitmapFor(it)
                albumInfoMap.put(key, it)
                return it
            }
        }

        getAlbumInfoAsync(songInfo)
        return null
    }

    private fun getAlbumInfoAsync(songInfo: SongInfo) {
        if (allowLastFM) {
            albumArtProvider.getAlbumInfoAsync(songInfo, object : AlbumArtCacheProvider.AlbumArtCallback {
                override fun onAlbumArtLoaded(bitmap: Bitmap?, songInfo: SongInfo) {
                    songInfoMap.put(SongInfoDao.getSongInfoKey(songInfo), songInfo)
                    songInfo.albumInfo?.let {
                        albumInfoMap.put(AlbumInfoDao.getAlbumKey(it), it)
                    }
                }
            })
        }
    }

    private fun getAlbumBitmapFor(albumInfo: AlbumInfo): Bitmap? {
        albumInfo.albumBitmap?.let {
            return it
        }
        return albumArtProvider.attemptCacheFetch(albumInfo)
    }

    private fun addInfoIfNeededAndReturn(key: String, songInfo: SongInfo): SongInfo? {
        if (songInfoMap[key] == null) {
            songInfoMap.put(key, songInfo)
        }

        return songInfoMap[key]
    }

    fun saveFavorite(songInfo: SongInfo) {
        val key = SongInfoDao.getSongInfoKey(songInfo)
        addInfoIfNeededAndReturn(key, songInfo)?.let {
            it.favorite = songInfo.favorite
            if (!it.favorite) {
                it.isExpanded = false
            }
            songInfoMap[key] = it
            songInfoDao.insertOrUpdate(songInfo)
        }
    }

    fun saveExpandedOrCollapsed(songInfo: SongInfo) {
        val key = SongInfoDao.getSongInfoKey(songInfo)
        addInfoIfNeededAndReturn(key, songInfo)?.let {
            it.isExpanded = songInfo.isExpanded
            songInfoMap[key] = it
            songInfoDao.insertOrUpdate(songInfo)
        }
    }
}