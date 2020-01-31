package com.philpot.nowplayinghistory.model

import android.graphics.Bitmap

/**
 * Created by MattPhilpot on 12/13/2017.
 */
data class ArtistInfo(var name: String,
                      var info: String,
                      var artistArtPath: String? = null,
                      var artistBitmap: Bitmap? = null)