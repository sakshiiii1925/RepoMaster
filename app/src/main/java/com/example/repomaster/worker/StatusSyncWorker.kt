package com.example.repomaster.worker

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.repomaster.repository.VehicleRepository

class StatusSyncWorker(
    appContext: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(
    appContext,
    workerParams
) {

    override suspend fun doWork(): Result {

        val repository =
            VehicleRepository(applicationContext)

        return try {

            val success =
                repository.syncPendingStatuses()

            if (success) {

                Result.success()

            } else {

                Result.retry()

            }

        } catch (e: Exception) {

            Result.retry()
        }
    }
}