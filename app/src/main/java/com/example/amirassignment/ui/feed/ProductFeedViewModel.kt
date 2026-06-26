package com.example.amirassignment.ui.feed



import androidx.lifecycle.ViewModel

import androidx.lifecycle.viewModelScope

import androidx.paging.cachedIn

import com.example.amirassignment.data.repository.ProductRepositoryImpl.Companion.ALL_CATEGORIES

import com.example.amirassignment.domain.model.InteractionType

import com.example.amirassignment.domain.model.Product

import com.example.amirassignment.domain.repository.AnalyticsRepository

import com.example.amirassignment.domain.repository.CartRepository

import com.example.amirassignment.domain.repository.ProductRepository

import dagger.hilt.android.lifecycle.HiltViewModel

import kotlinx.coroutines.flow.MutableStateFlow

import kotlinx.coroutines.flow.StateFlow

import kotlinx.coroutines.flow.asStateFlow

import kotlinx.coroutines.flow.combine

import kotlinx.coroutines.flow.update

import kotlinx.coroutines.launch

import javax.inject.Inject



@HiltViewModel

class ProductFeedViewModel @Inject constructor(

    private val productRepository: ProductRepository,

    private val analyticsRepository: AnalyticsRepository,

    private val cartRepository: CartRepository,

) : ViewModel() {



    private val _uiState = MutableStateFlow(ProductFeedUiState())

    val uiState: StateFlow<ProductFeedUiState> = _uiState.asStateFlow()



    val products = productRepository.getProductsPaging().cachedIn(viewModelScope)



    init {

        viewModelScope.launch {

            productRepository.observeHasCachedProducts().collect { hasCache ->

                if (!hasCache) {

                    refreshProductsInternal()

                } else {

                    _uiState.update { it.copy(showShimmer = false) }

                }

            }

        }

        viewModelScope.launch {

            productRepository.observeCategories().collect { slugs ->

                val withAll = listOf(ALL_CATEGORIES) + slugs

                _uiState.update { state ->

                    state.copy(

                        categories = withAll,

                        selectedCategory = state.selectedCategory.takeIf { it in withAll }

                            ?: ALL_CATEGORIES,

                    )

                }

            }

        }

        viewModelScope.launch {

            cartRepository.observeItemCount().collect { count ->

                _uiState.update { it.copy(cartItemCount = count) }

            }

        }

        viewModelScope.launch {

            cartRepository.observeCart().collect { items ->

                _uiState.update { it.copy(cartProductIds = items.map { item -> item.productId }.toSet()) }

            }

        }

    }



    fun selectCategory(category: String) {

        productRepository.setCategoryFilter(category)

        _uiState.update { it.copy(selectedCategory = category, expandedProductId = null) }

    }



    fun toggleProductExpanded(productId: Int) {

        _uiState.update { state ->

            state.copy(

                expandedProductId = if (state.expandedProductId == productId) null else productId,

            )

        }

    }



    fun refresh() {

        viewModelScope.launch {

            refreshProductsInternal()

        }

    }



    private suspend fun refreshProductsInternal() {

        _uiState.update { it.copy(isRefreshing = true, errorMessage = null) }

        productRepository.refreshProducts()

            .onSuccess {

                _uiState.update {

                    it.copy(

                        isRefreshing = false,

                        showShimmer = false,

                        snackbarMessage = null,

                    )

                }

            }

            .onFailure { error ->

                _uiState.update {

                    it.copy(

                        isRefreshing = false,

                        errorMessage = error.message ?: "Unable to refresh catalog",

                        snackbarMessage = "Showing cached products",

                        showShimmer = false,

                    )

                }

            }

    }



    fun onPagingError(message: String?) {

        if (message == null) {

            _uiState.update { it.copy(showShimmer = false) }

            return

        }

        _uiState.update {

            it.copy(

                errorMessage = message,

                snackbarMessage = "Showing cached products",

                showShimmer = false,

            )

        }

    }



    fun logInteraction(type: InteractionType, productId: Int?) {

        if (type == InteractionType.JOIN_POOL) {

            joinPool(productId)

            return

        }

        viewModelScope.launch {

            analyticsRepository.logEvent(type, productId)

            _uiState.update {

                it.copy(snackbarMessage = "Interaction logged: ${type.name.replace('_', ' ')}")

            }

        }

    }



    fun joinPool(productId: Int?) {

        viewModelScope.launch {

            analyticsRepository.logEvent(InteractionType.JOIN_POOL, productId)

            _uiState.update { state ->

                state.copy(

                    snackbarMessage = "Pool joined...",

                    joinedPoolProductIds = if (productId != null) {

                        state.joinedPoolProductIds + productId

                    } else {

                        state.joinedPoolProductIds

                    },

                    flashPoolJoined = if (productId == null) true else state.flashPoolJoined,

                )

            }

        }

    }



    fun addToCart(product: Product) {

        viewModelScope.launch {

            cartRepository.addProduct(product)

            analyticsRepository.logEvent(InteractionType.ADD_TO_CART, product.id)

            _uiState.update { it.copy(snackbarMessage = "Added to cart") }

        }

    }



    fun consumeSnackbar() {

        _uiState.update { it.copy(snackbarMessage = null) }

    }

}

