package com.philpot.nowplayinghistory.model

import android.graphics.Bitmap

/**
 * Created by MattPhilpot on 11/9/2017.
 */
data class AlbumInfo(val title: String,
                     val artist: String,
                     val year: String? = null,
                     var albumArtPath: String? = null,
                     var albumBitmap: Bitmap? = null)