package com.example.amirassignment.domain.model

import androidx.compose.runtime.Immutable

@Immutable
data class Product(

    val id: Int,

    val title: String,

    val price: Double,

    val imageUrl: String,

    val category: String,

    val brand: String = "",

    val inventoryCount: Int,

    val description: String = "",

    val discountPercentage: Double = 0.0,

)

