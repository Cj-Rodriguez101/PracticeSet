package com.example.practiceset2.worker

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.practiceset2.network.GoRestNetwork
import timber.log.Timber

class CreateLatestNotificationWorker(ctx: Context, params: WorkerParameters):
    CoroutineWorker(ctx, params) {

    override suspend fun doWork(): Result {
        val appContext = applicationContext

        //get the first user and display in a notification
        return try {
            val networkClient = GoRestNetwork.theGoRestDb
            val firstUser = networkClient.getListOfUsers(page = 1).await()[0]
            makeStatusNotification(firstUser, appContext)
            Result.success()
        } catch (ex: Exception){
            Timber.e(ex)
            Result.failure()
        }
    }
}