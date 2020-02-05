package com.philpot.nowplayinghistory.model

import androidx.room.*
import org.joda.time.DateTime

/**
 * Created by MattPhilpot on 11/6/2017.
 */
@Entity(foreignKeys =  [
    ForeignKey(entity = Artist::class, parentColumns = ["name"], childColumns = ["artist"]),
    ForeignKey(entity = Album::class, parentColumns = ["id"], childColumns = ["albumId"])
])
data class Song(@ColumnInfo(name = "title") var title: String,
                @ColumnInfo(name = "artist") var artist: String,
                @ColumnInfo(name = "lastHeard") var lastHeard: DateTime,
                @ColumnInfo(name = "currentHeard") var currentHeard: DateTime,
                @ColumnInfo(name = "heardCount") var heardCount: Long,
                @ColumnInfo(name = "favorite") var favorite: Boolean,
                @ColumnInfo(name = "isExpanded") var isExpanded: Boolean,
                @ColumnInfo(name = "albumId") var albumId: Int) {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    var id: Int = 0
}
