package com.example.amirassignment.data.remote

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import kotlinx.serialization.Serializable
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DummyJsonApi @Inject constructor(
    private val client: HttpClient,
) {
    suspend fun getProducts(limit: Int = 200, skip: Int = 0): ProductsResponseDto =
        client.get("$BASE_URL/products") {
            parameter("limit", limit)
            parameter("skip", skip)
        }.body()


    suspend fun uploadAnalytics(payload: AnalyticsUploadPayload) {
        val baseUrl = "https://script.google.com/macros/s/AKfycbz92UIf8bPsEHsuRk8VbEAWMNGPwmWqHJYnz4ijVgoiavk9CH2aVxFptfbp-JrcbBvC/exec"

        if (payload.events.isEmpty()) return

        val requestBody = AnalyticsUploadRequest(
            title = "analytics_batch",
            userId = 1,
            timestamp = System.currentTimeMillis(),
            events = payload.events
        )

        client.post(baseUrl) {
            contentType(ContentType.Application.Json)
            setBody(requestBody)
        }
    }

    companion object {
        const val BASE_URL = "https://dummyjson.com"
        const val CATALOG_FETCH_LIMIT = 100
    }
}

@Serializable
data class AnalyticsUploadRequest(
    val title: String,
    val userId: Int,
    val timestamp: Long,
    val events: List<AnalyticsEventDto>
)

