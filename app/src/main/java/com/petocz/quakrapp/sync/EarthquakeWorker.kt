package com.petocz.quakrapp.sync

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.petocz.quakrapp.QuakrApplication

class EarthquakeWorker(
    appContext: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {

        return try {

            val application = applicationContext as QuakrApplication

            val syncManager = SyncManager(
                repository = application.earthquakeRepository,
                syncWindowDao = application.database.syncWindowDao()
            )

            syncManager.syncMissingWindows()

            Result.success()

        } catch (e: Exception) {

            e.printStackTrace()

            Result.retry()
        }
    }
}