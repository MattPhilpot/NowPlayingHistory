package com.philpot.nowplayinghistory.info

import android.graphics.Bitmap
import com.philpot.nowplayinghistory.model.AlbumInfo
import com.philpot.nowplayinghistory.model.HistoryItem
import com.philpot.nowplayinghistory.model.SongInfo

/**
 * Created by MattPhilpot on 11/8/2017.
 */
interface AlbumArtCacheProvider {

    fun getAlbumInfoAsync(item: HistoryItem, callback: AlbumArtCallback?)
    fun getAlbumInfoAsync(songInfo: SongInfo, callback: AlbumArtCallback?)

    fun attemptCacheFetch(item: HistoryItem): Bitmap?
    fun attemptCacheFetch(songInfo: SongInfo): Bitmap?
    fun attemptCacheFetch(albumInfo: AlbumInfo?): Bitmap?

    interface AlbumArtCallback {
        fun onAlbumArtLoaded(bitmap: Bitmap?, songInfo: SongInfo)
    }

    fun getAllAlbumArtAsync()
}