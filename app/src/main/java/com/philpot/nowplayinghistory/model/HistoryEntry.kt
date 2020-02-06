package com.philpot.nowplayinghistory.model

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import org.joda.time.DateTime

/**
 * Created by colse on 10/29/2017.
 */
@Entity(tableName = "HistoryEntries",
    foreignKeys = [
        ForeignKey(entity = SongInfo::class, parentColumns = ["id"], childColumns = ["songId"]),
        ForeignKey(entity = ArtistInfo::class, parentColumns = ["name"], childColumns = ["artist"]),
        ForeignKey(entity = AlbumInfo::class, parentColumns = ["id"], childColumns = ["albumId"])
])
data class HistoryEntry(@ColumnInfo(name = "timestamp") var timestamp: DateTime,
                        @ColumnInfo(name = "songId") var songId: Long,
                        @ColumnInfo(name = "artist") var artist: String,
                        @ColumnInfo(name = "albumId") var albumId: Long? = null,
                        @Embedded var location: HistoryEntryLocation? = null) {

    @PrimaryKey(autoGenerate = true) var id: Long = 0
}
