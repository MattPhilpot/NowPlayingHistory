package com.philpot.nowplayinghistory.di

import com.philpot.nowplayinghistory.db2.NowPlayingDatabase
import com.philpot.nowplayinghistory.db2.dao.AlbumDao
import com.philpot.nowplayinghistory.db2.dao.ArtistDao
import com.philpot.nowplayinghistory.db2.dao.HistoryDao
import com.philpot.nowplayinghistory.db2.dao.SongDao
import org.kodein.di.Kodein
import org.kodein.di.erased.bind
import org.kodein.di.erased.instance
import org.kodein.di.erased.singleton

val databaseModule = Kodein.Module("DatabaseModule") {

    bind<SongDao>() with singleton {
        instance<NowPlayingDatabase>().songDao()
    }

    bind<AlbumDao>() with singleton {
        instance<NowPlayingDatabase>().albumDao()
    }

    bind<ArtistDao>() with singleton {
        instance<NowPlayingDatabase>().artistDao()
    }

    bind<HistoryDao>() with singleton {
        instance<NowPlayingDatabase>().historyDao()
    }
}
