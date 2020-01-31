package com.philpot.nowplayinghistory.model

import android.content.Context
import com.philpot.nowplayinghistory.R

/**
 * Created by colse on 11/1/2017.
 */
enum class MusicAppPreference(val nameRes: Int, val packageName: String?, val color: Int) {
    None(R.string.app_name_none, null, R.color.colorPrimary),
    Default(R.string.app_name_default, null, R.color.colorPrimary),
    YouTubeMusic(R.string.app_name_youtubemusic, "com.google.android.apps.youtube.music", R.color.app_color_youtube_music),
    YouTube(R.string.app_name_youtube, "com.google.android.youtube", R.color.app_color_youtube),
    GoogleMusic(R.string.app_name_googlemusic, "com.google.android.music", R.color.app_color_google_music),
    Spotify(R.string.app_name_spotify, "com.spotify.music", R.color.app_color_spotify),
    PandoraMusic(R.string.app_name_pandoramusic, "com.pandora.android", R.color.app_color_pandoramusic),
    //AmazonMusic(R.string.app_name_amazonmusic, "com.amazon.mp3", R.color.app_color_amazonmusic),
    Deezer(R.string.app_name_deezer, "deezer.android.app", R.color.app_color_deezer),
    SoundCloud(R.string.app_name_soundcloud, "com.soundcloud.android", R.color.app_color_soundcloud),
    AppleMusic(R.string.app_name_applemusic, "com.apple.android.music", R.color.app_color_applemusic);

    companion object {
        fun getAvailableList(context: Context): List<String> =//val retVal = mutableListOf<String>()
                MusicAppPreference.values().map { context.getString(it.nameRes) }

        fun getFromOrdinal(ordinal: Int?): MusicAppPreference {
            ordinal?.let { ord ->
                return MusicAppPreference.values().firstOrNull { it.ordinal == ord } ?: GoogleMusic
            }
            return GoogleMusic
        }
    }

}