package com.example.amirassignment.data.local

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import kotlinx.coroutines.flow.Flow

@Dao
interface ProductDao {
    @Query(
        """
        SELECT * FROM products
        WHERE (:category = 'ALL' OR category = :category)
        ORDER BY id ASC
        """,
    )
    fun pagingSource(category: String): PagingSource<Int, ProductEntity>

    @Query("SELECT DISTINCT category FROM products ORDER BY category ASC")
    fun observeCategories(): Flow<List<String>>

    @Query("SELECT COUNT(*) > 0 FROM products")
    fun observeHasProducts(): Flow<Boolean>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(products: List<ProductEntity>)

    @Query("DELETE FROM products")
    suspend fun clearAll()

    @Transaction
    suspend fun replaceAll(products: List<ProductEntity>) {
        clearAll()
        insertAll(products)
    }
}
