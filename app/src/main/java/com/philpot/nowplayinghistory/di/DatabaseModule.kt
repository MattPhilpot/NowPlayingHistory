package com.philpot.nowplayinghistory.di

import com.philpot.nowplayinghistory.db2.NowPlayingDatabase
import com.philpot.nowplayinghistory.db2.dao.AlbumInfoDao
import com.philpot.nowplayinghistory.db2.dao.ArtistInfoDao
import com.philpot.nowplayinghistory.db2.dao.HistoryDao
import com.philpot.nowplayinghistory.db2.dao.ParameterDao
import com.philpot.nowplayinghistory.db2.dao.SongInfoDao
import com.philpot.nowplayinghistory.db2.manager.ParameterManager
import org.kodein.di.Kodein
import org.kodein.di.erased.bind
import org.kodein.di.erased.instance
import org.kodein.di.erased.singleton

val databaseModule = Kodein.Module("DatabaseModule") {

    bind<SongInfoDao>() with singleton {
        instance<NowPlayingDatabase>().songDao()
    }

    bind<AlbumInfoDao>() with singleton {
        instance<NowPlayingDatabase>().albumDao()
    }

    bind<ArtistInfoDao>() with singleton {
        instance<NowPlayingDatabase>().artistDao()
    }

    bind<HistoryDao>() with singleton {
        instance<NowPlayingDatabase>().historyDao()
    }

    bind<ParameterDao>() with singleton {
        instance<NowPlayingDatabase>().parameterDao()
    }

    bind<ParameterManager>() with singleton {
        ParameterManager(parameterDao = instance(), coroutineContextProvider = instance())
    }
}
