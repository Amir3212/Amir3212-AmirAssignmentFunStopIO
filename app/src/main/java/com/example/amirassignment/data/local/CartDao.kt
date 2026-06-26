package com.example.amirassignment.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface CartDao {
    @Query("SELECT * FROM cart_items ORDER BY title ASC")
    fun observeItems(): Flow<List<CartItemEntity>>

    @Query("SELECT COALESCE(SUM(quantity), 0) FROM cart_items")
    fun observeItemCount(): Flow<Int>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(item: CartItemEntity)

    @Query("UPDATE cart_items SET quantity = :quantity WHERE productId = :productId")
    suspend fun updateQuantity(productId: Int, quantity: Int)

    @Query("DELETE FROM cart_items WHERE productId = :productId")
    suspend fun remove(productId: Int)

    @Query("DELETE FROM cart_items")
    suspend fun clearAll()

    @Query("SELECT * FROM cart_items WHERE productId = :productId LIMIT 1")
    suspend fun getByProductId(productId: Int): CartItemEntity?
}
