package com.philpot.nowplayinghistory.db2.manager

import android.graphics.Bitmap
import com.philpot.nowplayinghistory.db.dao.AlbumInfoDao
import com.philpot.nowplayinghistory.db.dao.SongInfoDao
import com.philpot.nowplayinghistory.info.AlbumArtCacheProvider
import com.philpot.nowplayinghistory.model.Album
import com.philpot.nowplayinghistory.model.HistoryEntry
import com.philpot.nowplayinghistory.model.Song

/**
 * Created by MattPhilpot on 11/9/2017.
 */
class SongAlbumManager(private val songInfoDao: SongInfoDao,
                       private val albumInfoDao: AlbumInfoDao,
                       private val albumArtProvider: AlbumArtCacheProvider) {

    companion object {
        private val songInfoMap = HashMap<String, Song>()
        private val albumInfoMap = HashMap<String, Album>()
    }

    var allowLastFM = false

    fun getSongInfoFor(entry: HistoryEntry): Song {
        val key = SongInfoDao.getSongInfoKey(entry)
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

    fun getAlbumInfoFor(song: Song): Album? {
        song.album?.let {
            val key = AlbumInfoDao.getAlbumKey(song.artist, it)
            albumInfoMap[key]?.let {
                if (it.albumBitmap == null) {
                    it.albumBitmap = getAlbumBitmapFor(it)
                }

                if (it.albumBitmap == null) {
                    getAlbumInfoAsync(song)
                }
                return it
            }

            albumInfoDao.getAlbumInfoFrom(song)?.let {
                it.albumBitmap = getAlbumBitmapFor(it)
                albumInfoMap.put(key, it)
                return it
            }
        }

        getAlbumInfoAsync(song)
        return null
    }

    private fun getAlbumInfoAsync(song: Song) {
        if (allowLastFM) {
            albumArtProvider.getAlbumInfoAsync(song, object : AlbumArtCacheProvider.AlbumArtCallback {
                override fun onAlbumArtLoaded(bitmap: Bitmap?, song: Song) {
                    songInfoMap.put(SongInfoDao.getSongInfoKey(song), song)
                    song.album?.let {
                        albumInfoMap.put(AlbumInfoDao.getAlbumKey(it), it)
                    }
                }
            })
        }
    }

    private fun getAlbumBitmapFor(album: Album): Bitmap? {
        album.albumBitmap?.let {
            return it
        }
        return albumArtProvider.attemptCacheFetch(album)
    }

    private fun addInfoIfNeededAndReturn(key: String, song: Song): Song? {
        if (songInfoMap[key] == null) {
            songInfoMap.put(key, song)
        }

        return songInfoMap[key]
    }

    fun saveFavorite(song: Song) {
        val key = SongInfoDao.getSongInfoKey(song)
        addInfoIfNeededAndReturn(key, song)?.let {
            it.favorite = song.favorite
            if (!it.favorite) {
                it.isExpanded = false
            }
            songInfoMap[key] = it
            songInfoDao.insertOrUpdate(song)
        }
    }

    fun saveExpandedOrCollapsed(song: Song) {
        val key = SongInfoDao.getSongInfoKey(song)
        addInfoIfNeededAndReturn(key, song)?.let {
            it.isExpanded = song.isExpanded
            songInfoMap[key] = it
            songInfoDao.insertOrUpdate(song)
        }
    }
}