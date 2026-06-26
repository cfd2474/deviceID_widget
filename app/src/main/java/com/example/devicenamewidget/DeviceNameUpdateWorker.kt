package com.example.devicenamewidget

import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.provider.Settings
import androidx.work.Constraints
import androidx.work.CoroutineWorker
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters

class DeviceNameUpdateWorker(
    private val context: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        // Send a broadcast to the AppWidgetProvider to trigger an update
        val intent = Intent(context, DeviceNameWidgetProvider::class.java).apply {
            action = AppWidgetManager.ACTION_APPWIDGET_UPDATE
            val ids = AppWidgetManager.getInstance(context).getAppWidgetIds(
                ComponentName(context, DeviceNameWidgetProvider::class.java)
            )
            putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, ids)
        }
        context.sendBroadcast(intent)

        // Reschedule the worker to listen for the next change
        schedule(context)

        return Result.success()
    }

    companion object {
        private const val WORK_NAME = "DeviceNameUpdateWork"

        fun schedule(context: Context) {
            val constraints = Constraints.Builder()
                .addContentUriTrigger(Settings.Global.getUriFor(Settings.Global.DEVICE_NAME), true)
                .build()

            val workRequest = OneTimeWorkRequestBuilder<DeviceNameUpdateWorker>()
                .setConstraints(constraints)
                .build()

            WorkManager.getInstance(context).enqueueUniqueWork(
                WORK_NAME,
                ExistingWorkPolicy.REPLACE,
                workRequest
            )
        }
        
        fun cancel(context: Context) {
            WorkManager.getInstance(context).cancelUniqueWork(WORK_NAME)
        }
    }
}
