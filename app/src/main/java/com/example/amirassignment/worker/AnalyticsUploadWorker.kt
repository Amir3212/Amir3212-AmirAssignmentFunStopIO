package com.example.amirassignment.worker

import android.content.Context
import android.util.Log
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.amirassignment.domain.repository.AnalyticsRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

@HiltWorker
class AnalyticsUploadWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted params: WorkerParameters,
    private val analyticsRepository: AnalyticsRepository,
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        Log.d(TAG, "doWork started (attempt=${runAttemptCount}, id=${id})")
        return analyticsRepository.uploadPendingEvents()
            .fold(
                onSuccess = { uploadedCount ->
                    Log.d(TAG, "doWork finished: success, uploadedCount=$uploadedCount")
                    Result.success()
                },
                onFailure = { error ->
                    Log.d(TAG, "doWork finished: failure, will retry", error)
                    Result.retry()
                },
            )
    }

    companion object {
        const val WORK_NAME = "analytics_upload_work"
        private const val TAG = "AnalyticsUploadWorker"
    }
}
