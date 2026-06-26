package com.example.amirassignment

import android.app.Application
import android.util.Log
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import coil.ImageLoader
import coil.ImageLoaderFactory
import com.example.amirassignment.worker.AnalyticsUploadWorker
import dagger.hilt.android.HiltAndroidApp
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@HiltAndroidApp
class AmirApplication : Application(), Configuration.Provider, ImageLoaderFactory {

    @Inject
    lateinit var workerFactory: HiltWorkerFactory

    @Inject
    lateinit var injectedWorkManagerConfiguration: Configuration

    @Inject
    lateinit var imageLoader: ImageLoader

    override val workManagerConfiguration: Configuration
        get() = injectedWorkManagerConfiguration

    override fun newImageLoader(): ImageLoader = imageLoader

    override fun onCreate() {
        super.onCreate()
        scheduleAnalyticsUpload()
    }

    private fun scheduleAnalyticsUpload() {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.UNMETERED)
            .setRequiresCharging(true)
            .build()

        val request = PeriodicWorkRequestBuilder<AnalyticsUploadWorker>(15, TimeUnit.MINUTES)
            .setConstraints(constraints)
            .build()

        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
            AnalyticsUploadWorker.WORK_NAME,
            ExistingPeriodicWorkPolicy.KEEP,
            request,
        )
        Log.d(TAG, "periodic work enqueued: ${AnalyticsUploadWorker.WORK_NAME}")
    }

    companion object {
        private const val TAG = "AmirApplication"
    }
}
