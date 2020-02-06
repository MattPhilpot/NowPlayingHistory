package com.philpot.nowplayinghistory.db2.manager

import android.graphics.Bitmap
import android.util.LongSparseArray
import com.philpot.nowplayinghistory.db2.dao.AlbumInfoDao
import com.philpot.nowplayinghistory.db2.dao.SongInfoDao
import com.philpot.nowplayinghistory.model.AlbumInfo
import com.philpot.nowplayinghistory.model.HistoryEntry
import com.philpot.nowplayinghistory.model.SongInfo

/**
 * Created by MattPhilpot on 11/9/2017.
 */
/*
class SongAlbumManager(private val songInfoDao: SongInfoDao,
                       private val albumInfoDao: AlbumInfoDao) {

    companion object {
        private val songInfoMap = LongSparseArray
        private val albumInfoMap = HashMap<String, AlbumInfo>()
    }

    var allowLastFM = false

    fun getSongInfoFor(entry: HistoryEntry): SongInfo {
        //val key = SongInfoDao.getSongInfoKey(entry)
        songInfoMap[key]?.let {
            if (it.album?.albumBitmap == null) {
                it.album = getAlbumInfoFor(it)
            }
            return it
        }

        songInfoDao.getSongInfoFrom(entry).let {
            it.album = getAlbumInfoFor(it)

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
                    songInfo.album?.let {
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
*/