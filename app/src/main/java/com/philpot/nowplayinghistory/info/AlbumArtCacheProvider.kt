package com.philpot.nowplayinghistory.info

import android.graphics.Bitmap
import com.philpot.nowplayinghistory.model.Album
import com.philpot.nowplayinghistory.model.HistoryEntry
import com.philpot.nowplayinghistory.model.Song

/**
 * Created by MattPhilpot on 11/8/2017.
 */
interface AlbumArtCacheProvider {

    fun getAlbumInfoAsync(entry: HistoryEntry, callback: AlbumArtCallback?)
    fun getAlbumInfoAsync(song: Song, callback: AlbumArtCallback?)

    fun attemptCacheFetch(entry: HistoryEntry): Bitmap?
    fun attemptCacheFetch(song: Song): Bitmap?
    fun attemptCacheFetch(album: Album?): Bitmap?

    interface AlbumArtCallback {
        fun onAlbumArtLoaded(bitmap: Bitmap?, song: Song)
    }

    fun getAllAlbumArtAsync()
}