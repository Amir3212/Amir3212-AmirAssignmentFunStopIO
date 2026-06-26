package com.example.amirassignment.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface RemoteKeysDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(keys: List<RemoteKeys>)

    @Query("SELECT * FROM remote_keys WHERE productId = :id")
    suspend fun remoteKeysProductId(id: Int): RemoteKeys?

    @Query("DELETE FROM remote_keys")
    suspend fun clearAll()
}

@Dao
interface InteractionLogDao {
    @Insert
    suspend fun insert(log: InteractionLogEntity): Long

    @Query("SELECT * FROM interaction_logs WHERE synced = 0 ORDER BY timestamp ASC")
    suspend fun getUnsynced(): List<InteractionLogEntity>

    @Query("UPDATE interaction_logs SET synced = 1 WHERE id IN (:ids)")
    suspend fun markSynced(ids: List<Long>)

    @Query("SELECT COUNT(*) FROM interaction_logs WHERE synced = 0")
    suspend fun countUnsynced(): Int
}
