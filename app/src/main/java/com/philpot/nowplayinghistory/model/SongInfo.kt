package com.philpot.nowplayinghistory.model

import androidx.room.*

/**
 * Created by MattPhilpot on 11/6/2017.
 */
@Entity(foreignKeys =  [
    ForeignKey(entity = ArtistInfo::class, parentColumns = ["name"], childColumns = ["artist"]),
    ForeignKey(entity = AlbumInfo::class, parentColumns = ["id"], childColumns = ["albumId"])],
    indices = [Index(value = ["title", "artist"])])
data class SongInfo(@ColumnInfo(name = "title") var title: String,
                    @ColumnInfo(name = "artist") var artist: String,
                    //@ColumnInfo(name = "lastHeard") var lastHeard: DateTime,
                    //@ColumnInfo(name = "currentHeard") var currentHeard: DateTime,
                    @ColumnInfo(name = "heardCount", defaultValue = "1") var heardCount: Long = 1, //default to 1
                    @ColumnInfo(name = "favorite") var favorite: Boolean = false,
                    @ColumnInfo(name = "isExpanded") var isExpanded: Boolean = false,
                    @ColumnInfo(name = "albumId") var albumId: Long? = null) {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    var id: Long = 0
}
