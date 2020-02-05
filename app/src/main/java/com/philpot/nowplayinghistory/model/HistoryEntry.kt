package com.philpot.nowplayinghistory.model

import androidx.room.*
import org.joda.time.DateTime

/**
 * Created by colse on 10/29/2017.
 */
@Entity(foreignKeys = [
    ForeignKey(entity = Album::class, parentColumns = ["id"], childColumns = ["songId"])
])
data class HistoryEntry(@ColumnInfo(name = "timestamp") var timestamp: DateTime,
                        @ColumnInfo(name = "songId") var songId: Int,
                        @Embedded var location: HistoryEntryLocation? = null) {

    @PrimaryKey(autoGenerate = true) var id: Int = 0
}
