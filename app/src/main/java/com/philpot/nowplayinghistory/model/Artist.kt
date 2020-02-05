package com.philpot.nowplayinghistory.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Created by MattPhilpot on 12/13/2017.
 */
@Entity
data class Artist(@PrimaryKey
                  @ColumnInfo(name = "name") var name: String,
                  @ColumnInfo(name = "info") var info: String,
                  @ColumnInfo(name = "artistArtPath") var artistArtPath: String? = null)
