package com.philpot.nowplayinghistory.info

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import coil.Coil
import coil.api.load
import kotlinx.coroutines.coroutineScope
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import org.kodein.di.android.closestKodein

class AlbumArtDownloadWorker(private val context: Context, params: WorkerParameters) : CoroutineWorker(context, params), KodeinAware {

    private val parentKodein by closestKodein(context)

    override val kodein: Kodein = Kodein.lazy {
        extend(parentKodein)
    }



    override suspend fun doWork(): Result = coroutineScope {
        try {


            Result.success()
        } catch (t: Throwable) {

            Result.failure()
        }
    }
}