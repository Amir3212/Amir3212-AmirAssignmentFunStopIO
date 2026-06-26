package com.example.amirassignment.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "products")
data class ProductEntity(
    @PrimaryKey val id: Int,
    val title: String,
    val price: Double,
    val imageUrl: String,
    val category: String,
    val brand: String = "",
    val displayCategory: String,
    val inventoryCount: Int,
    val description: String = "",
    val discountPercentage: Double = 0.0,
    val cachedAt: Long,
)
