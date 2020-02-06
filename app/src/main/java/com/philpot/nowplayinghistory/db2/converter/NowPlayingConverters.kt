package com.philpot.nowplayinghistory.db2.converter

import androidx.room.TypeConverter
import org.joda.time.DateTime

class NowPlayingConverters {

    @TypeConverter
    fun fromDateTime(value: DateTime): Long {
        return value.millis
    }

    @TypeConverter
    fun toDateTime(value: Long): DateTime {
        return DateTime(value)
    }
}