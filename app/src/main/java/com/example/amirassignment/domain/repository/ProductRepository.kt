package com.example.amirassignment.domain.repository



import androidx.paging.PagingData

import com.example.amirassignment.domain.model.Product

import kotlinx.coroutines.flow.Flow



interface ProductRepository {

    fun getProductsPaging(): Flow<PagingData<Product>>

    fun setCategoryFilter(category: String)

    fun observeCategories(): Flow<List<String>>

    suspend fun refreshProducts(): Result<Unit>

    fun observeHasCachedProducts(): Flow<Boolean>

}

