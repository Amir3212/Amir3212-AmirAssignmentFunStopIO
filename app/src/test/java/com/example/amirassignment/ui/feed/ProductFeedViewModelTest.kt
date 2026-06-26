package com.example.amirassignment.ui.feed

import com.example.amirassignment.domain.repository.AnalyticsRepository
import com.example.amirassignment.domain.repository.CartRepository
import com.example.amirassignment.domain.repository.ProductRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class ProductFeedViewModelTest {

    private val productRepository = mockk<ProductRepository>(relaxed = true)
    private val analyticsRepository = mockk<AnalyticsRepository>(relaxed = true)
    private val cartRepository = mockk<CartRepository>(relaxed = true)
    private val testDispatcher = UnconfinedTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        every { productRepository.observeHasCachedProducts() } returns flowOf(true)
        every { productRepository.getProductsPaging() } returns flowOf()
        every { productRepository.observeCategories() } returns flowOf(listOf("beauty"))
        every { cartRepository.observeItemCount() } returns flowOf(0)
        every { cartRepository.observeCart() } returns flowOf(emptyList())
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun selectCategory_updatesUiStateAndRepository() = runTest {
        val viewModel = ProductFeedViewModel(productRepository, analyticsRepository, cartRepository)
        viewModel.selectCategory("beauty")
        assertEquals("beauty", viewModel.uiState.value.selectedCategory)
        verify { productRepository.setCategoryFilter("beauty") }
    }

    @Test
    fun refreshProducts_success_clearsRefreshing() = runTest {
        coEvery { productRepository.refreshProducts() } returns Result.success(Unit)
        val viewModel = ProductFeedViewModel(productRepository, analyticsRepository, cartRepository)
        viewModel.refresh()
        assertEquals(false, viewModel.uiState.value.isRefreshing)
    }

    @Test
    fun logInteraction_callsRepository() = runTest {
        val viewModel = ProductFeedViewModel(productRepository, analyticsRepository, cartRepository)
        viewModel.logInteraction(com.example.amirassignment.domain.model.InteractionType.JOIN_POOL, 5)
        coVerify { analyticsRepository.logEvent(com.example.amirassignment.domain.model.InteractionType.JOIN_POOL, 5) }
    }
}
