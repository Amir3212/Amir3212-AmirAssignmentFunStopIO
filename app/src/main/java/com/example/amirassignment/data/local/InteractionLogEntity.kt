package com.example.amirassignment.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "interaction_logs")
data class InteractionLogEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val type: String,
    val productId: Int?,
    val timestamp: Long,
    val synced: Boolean,
)
