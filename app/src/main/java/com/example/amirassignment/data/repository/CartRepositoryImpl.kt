package com.example.amirassignment.data.repository

import com.example.amirassignment.data.local.CartDao
import com.example.amirassignment.data.local.CartItemEntity
import com.example.amirassignment.domain.model.CartItem
import com.example.amirassignment.domain.model.Product
import com.example.amirassignment.domain.repository.CartRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CartRepositoryImpl @Inject constructor(
    private val cartDao: CartDao,
) : CartRepository {

    override fun observeCart(): Flow<List<CartItem>> =
        cartDao.observeItems().map { items -> items.map { it.toDomain() } }

    override fun observeItemCount(): Flow<Int> = cartDao.observeItemCount()

    override suspend fun addProduct(product: Product) = withContext(Dispatchers.IO) {
        val existing = cartDao.getByProductId(product.id)
        if (existing == null) {
            cartDao.upsert(
                CartItemEntity(
                    productId = product.id,
                    title = product.title,
                    price = product.price,
                    imageUrl = product.imageUrl,
                    quantity = 1,
                ),
            )
        } else {
            cartDao.updateQuantity(product.id, existing.quantity + 1)
        }
    }

    override suspend fun updateQuantity(productId: Int, quantity: Int) = withContext(Dispatchers.IO) {
        if (quantity <= 0) {
            cartDao.remove(productId)
        } else {
            cartDao.updateQuantity(productId, quantity)
        }
    }

    override suspend fun remove(productId: Int) = withContext(Dispatchers.IO) {
        cartDao.remove(productId)
    }

    override suspend fun clear() = withContext(Dispatchers.IO) {
        cartDao.clearAll()
    }

    private fun CartItemEntity.toDomain(): CartItem =
        CartItem(
            productId = productId,
            title = title,
            price = price,
            imageUrl = imageUrl,
            quantity = quantity,
        )
}
