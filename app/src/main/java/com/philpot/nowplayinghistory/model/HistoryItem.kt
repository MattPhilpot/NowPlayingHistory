package com.philpot.nowplayinghistory.model

import androidx.room.Embedded
import androidx.room.Relation

data class HistoryItem(@Embedded val historyEntry: HistoryEntry,
                       @Relation(parentColumn = "songId", entityColumn = "id")
                       val songInfo: SongInfo,
                       @Relation(parentColumn = "albumId", entityColumn = "id")
                       val albumInfo: AlbumInfo?)
