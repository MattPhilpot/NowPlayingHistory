package com.philpot.nowplayinghistory.model

import android.graphics.Bitmap
import androidx.room.*

/**
 * Created by MattPhilpot on 11/9/2017.
 */
@Entity(foreignKeys =  [
    ForeignKey(entity = Artist::class, parentColumns = ["name"], childColumns = ["artist"])
])
data class Album(@ColumnInfo(name = "title") val title: String,
                 @ColumnInfo(name = "artist") val artist: String,
                 @ColumnInfo(name = "year") val year: String? = null,
                 @ColumnInfo(name = "albumArtPath") var albumArtPath: String? = null,
                 @Ignore var albumBitmap: Bitmap? = null) {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    var id: Int = 0
}
