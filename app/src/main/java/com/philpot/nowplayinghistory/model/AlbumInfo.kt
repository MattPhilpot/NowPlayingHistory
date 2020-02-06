package com.philpot.nowplayinghistory.model

import android.graphics.Bitmap
import androidx.room.*

/**
 * Created by MattPhilpot on 11/9/2017.
 */
@Entity(foreignKeys =  [
    ForeignKey(entity = ArtistInfo::class, parentColumns = ["name"], childColumns = ["artist"])
])
data class AlbumInfo(@ColumnInfo(name = "title") val title: String,
                     @ColumnInfo(name = "artist") val artist: String,
                     @ColumnInfo(name = "year") val year: String?,
                     @ColumnInfo(name = "albumArtPath") var albumArtPath: String?) {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    var id: Long = 0
}
