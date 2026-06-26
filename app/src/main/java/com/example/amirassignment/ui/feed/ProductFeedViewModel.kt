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

    private var hasCachedProducts = false

    init {
        viewModelScope.launch {
            productRepository.observeHasCachedProducts().collect { hasCache ->
                hasCachedProducts = hasCache
                if (!hasCache) {
                    _uiState.update {
                        it.copy(catalogSyncState = CatalogSyncState.Syncing, errorMessage = null)
                    }
                    refreshProductsInternal()
                } else {
                    _uiState.update { it.copy(catalogSyncState = CatalogSyncState.Synced) }
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
            _uiState.update {
                it.copy(
                    catalogSyncState = CatalogSyncState.Syncing,
                    errorMessage = null,
                )
            }
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
                        catalogSyncState = CatalogSyncState.Synced,
                        snackbarMessage = null,
                    )
                }
            }
            .onFailure {
                _uiState.update {
                    it.copy(
                        isRefreshing = false,
                        catalogSyncState = if (hasCachedProducts) {
                            CatalogSyncState.Synced
                        } else {
                            CatalogSyncState.Failed
                        },
                        errorMessage = if (hasCachedProducts) {
                            it.errorMessage
                        } else {
                            CATALOG_LOAD_ERROR_MESSAGE
                        },
                        snackbarMessage = if (hasCachedProducts) "Showing cached products" else null,
                    )
                }
            }
    }

    fun onPagingError(message: String?) {
        if (message == null) return
        val state = _uiState.value
        if (state.catalogSyncState == CatalogSyncState.Syncing) return
        _uiState.update {
            it.copy(
                errorMessage = message,
                snackbarMessage = if (state.catalogSyncState == CatalogSyncState.Synced) {
                    "Showing cached products"
                } else {
                    it.snackbarMessage
                },
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

    companion object {
        const val CATALOG_LOAD_ERROR_MESSAGE = "Something went wrong. Please try again."
    }
}
