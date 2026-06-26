package com.example.amirassignment.domain.flash

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

data class FlashCountdownTick(
    val formatted: String,
    val remainingMs: Long,
    val isExpired: Boolean,
)

fun countdownFlow(endEpochMs: Long): Flow<FlashCountdownTick> = flow {
    while (true) {
        val remainingMs = (endEpochMs - System.currentTimeMillis()).coerceAtLeast(0)
        emit(
            FlashCountdownTick(
                formatted = formatRemaining(remainingMs),
                remainingMs = remainingMs,
                isExpired = remainingMs == 0L,
            ),
        )
        if (remainingMs == 0L) break
        delay(1000)
    }
}.flowOn(Dispatchers.Default)

fun formatRemaining(remainingMs: Long): String {
    val totalSeconds = remainingMs / 1000
    val hours = totalSeconds / 3600
    val minutes = (totalSeconds % 3600) / 60
    val seconds = totalSeconds % 60
    return "%02d:%02d:%02d".format(hours, minutes, seconds)
}
