package com.example.amirassignment.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.PagingSource
import androidx.paging.map
import com.example.amirassignment.data.local.ProductDao
import com.example.amirassignment.data.local.ProductEntity
import com.example.amirassignment.data.remote.DummyJsonApi
import com.example.amirassignment.data.remote.toDomain
import com.example.amirassignment.data.remote.toEntity
import com.example.amirassignment.domain.model.Product
import com.example.amirassignment.domain.repository.ProductRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ProductRepositoryImpl @Inject constructor(
    private val api: DummyJsonApi,
    private val productDao: ProductDao,
) : ProductRepository {

    private val categoryFilter = MutableStateFlow(ALL_CATEGORIES)
    private var currentPagingSource: PagingSource<Int, ProductEntity>? = null

    private val pagerFlow: Flow<PagingData<Product>> = Pager(
        config = PagingConfig(
            pageSize = PAGE_SIZE,
            enablePlaceholders = false,
        ),
        pagingSourceFactory = {
            DelayedProductPagingSource(
                delegate = productDao.pagingSource(categoryFilter.value),
            ).also { currentPagingSource = it }
        },
    ).flow.map { pagingData ->
        pagingData.map { entity -> entity.toDomain() }
    }

    override fun getProductsPaging(): Flow<PagingData<Product>> = pagerFlow

    override fun setCategoryFilter(category: String) {
        categoryFilter.value = category
        currentPagingSource?.invalidate()
    }

    override fun observeCategories(): Flow<List<String>> = productDao.observeCategories()

    override suspend fun refreshProducts(): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val response = api.getProducts(limit = DummyJsonApi.CATALOG_FETCH_LIMIT, skip = 0)
            val now = System.currentTimeMillis()
            productDao.replaceAll(response.products.map { it.toEntity(now) })
            currentPagingSource?.invalidate()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override fun observeHasCachedProducts(): Flow<Boolean> =
        productDao.observeHasProducts()

    companion object {
        const val ALL_CATEGORIES = "ALL"
        const val PAGE_SIZE = 10
    }
}
