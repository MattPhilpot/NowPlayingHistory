package com.philpot.nowplayinghistory.model

/**
 * Created by MattPhilpot on 11/6/2017.
 */
data class SongInfo(var title: String,
                    var artist: String,
                    var lastHeard: Long,
                    var currentHeard: Long,
                    var heardCount: Long,
                    var favorite: Boolean,
                    var isExpanded: Boolean,
                    var album: String? = null,
                    var albumInfo: AlbumInfo? = null)