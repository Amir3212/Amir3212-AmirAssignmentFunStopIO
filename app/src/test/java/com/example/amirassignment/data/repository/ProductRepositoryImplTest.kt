package com.example.amirassignment.data.repository

import com.example.amirassignment.data.local.ProductDao
import com.example.amirassignment.data.remote.DummyJsonApi
import com.example.amirassignment.data.remote.ProductDto
import com.example.amirassignment.data.remote.ProductsResponseDto
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertTrue
import org.junit.Test

class ProductRepositoryImplTest {

    @Test
    fun refreshProducts_persistsApiResponse() = runTest {
        val api = mockk<DummyJsonApi>()
        val productDao = mockk<ProductDao>(relaxed = true)
        val dto = ProductDto(
            id = 1,
            title = "Phone",
            price = 99.0,
            category = "smartphones",
            thumbnail = "https://example.com/img.png",
            stock = 50,
        )
        coEvery {
            api.getProducts(limit = DummyJsonApi.CATALOG_FETCH_LIMIT, skip = 0)
        } returns ProductsResponseDto(
            products = listOf(dto),
            total = 1,
            skip = 0,
            limit = 100,
        )

        val repository = ProductRepositoryImpl(api, productDao)
        val result = repository.refreshProducts()

        assertTrue(result.isSuccess)
        coVerify { productDao.replaceAll(match { entities -> entities.size == 1 && entities.first().id == 1 }) }
    }
}
