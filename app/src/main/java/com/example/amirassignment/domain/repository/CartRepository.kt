package com.example.amirassignment.domain.repository

import com.example.amirassignment.domain.model.CartItem
import com.example.amirassignment.domain.model.Product
import kotlinx.coroutines.flow.Flow

interface CartRepository {
    fun observeCart(): Flow<List<CartItem>>
    fun observeItemCount(): Flow<Int>
    suspend fun addProduct(product: Product)
    suspend fun updateQuantity(productId: Int, quantity: Int)
    suspend fun remove(productId: Int)
    suspend fun clear()
}
