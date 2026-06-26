package com.example.amirassignment.domain.repository

import com.example.amirassignment.domain.model.InteractionType

interface AnalyticsRepository {
    suspend fun logEvent(type: InteractionType, productId: Int?)
    suspend fun uploadPendingEvents(): Result<Int>
}
