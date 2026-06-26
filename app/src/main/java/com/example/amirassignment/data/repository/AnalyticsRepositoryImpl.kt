package com.example.amirassignment.data.repository

import com.example.amirassignment.data.local.InteractionLogDao
import com.example.amirassignment.data.local.InteractionLogEntity
import com.example.amirassignment.data.remote.AnalyticsEventDto
import com.example.amirassignment.data.remote.AnalyticsUploadPayload
import com.example.amirassignment.data.remote.DummyJsonApi
import com.example.amirassignment.domain.model.InteractionType
import com.example.amirassignment.domain.repository.AnalyticsRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AnalyticsRepositoryImpl @Inject constructor(
    private val interactionLogDao: InteractionLogDao,
    private val api: DummyJsonApi,
) : AnalyticsRepository {

    override suspend fun logEvent(type: InteractionType, productId: Int?) {
        withContext(Dispatchers.IO) {
            interactionLogDao.insert(
                InteractionLogEntity(
                    type = type.name,
                    productId = productId,
                    timestamp = System.currentTimeMillis(),
                    synced = false,
                ),
            )
        }
    }

    override suspend fun uploadPendingEvents(): Result<Int> = withContext(Dispatchers.IO) {
        try {
            val pending = interactionLogDao.getUnsynced()
            if (pending.isEmpty()) return@withContext Result.success(0)

            val payload = AnalyticsUploadPayload(
                events = pending.map {
                    AnalyticsEventDto(
                        type = it.type,
                        productId = it.productId,
                        timestamp = it.timestamp,
                    )
                },
            )
            api.uploadAnalytics(payload)
            interactionLogDao.markSynced(pending.map { it.id })
            Result.success(pending.size)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
