package com.example.amirassignment.domain.model

data class CartItem(
    val productId: Int,
    val title: String,
    val price: Double,
    val imageUrl: String,
    val quantity: Int,
) {
    val lineTotal: Double get() = price * quantity
}
