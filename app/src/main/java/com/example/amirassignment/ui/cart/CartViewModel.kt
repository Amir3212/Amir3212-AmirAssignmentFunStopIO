package com.example.amirassignment.ui.cart

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.amirassignment.domain.model.InteractionType
import com.example.amirassignment.domain.repository.AnalyticsRepository
import com.example.amirassignment.domain.repository.CartRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CartViewModel @Inject constructor(
    private val cartRepository: CartRepository,
    private val analyticsRepository: AnalyticsRepository,
) : ViewModel() {

    private val _snackbarMessage = MutableStateFlow<String?>(null)
    val snackbarMessage: StateFlow<String?> = _snackbarMessage.asStateFlow()

    val uiState: StateFlow<CartUiState> = cartRepository.observeCart()
        .map { items ->
            CartUiState(
                items = items,
                isEmpty = items.isEmpty(),
            )
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = CartUiState(),
        )

    val totalPrice: StateFlow<Double> = cartRepository.observeCart()
        .map { items -> items.sumOf { it.lineTotal } }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = 0.0,
        )

    val itemCount: StateFlow<Int> = cartRepository.observeItemCount()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = 0,
        )

    fun incrementQuantity(productId: Int, currentQuantity: Int) {
        viewModelScope.launch {
            cartRepository.updateQuantity(productId, currentQuantity + 1)
        }
    }

    fun decrementQuantity(productId: Int, currentQuantity: Int) {
        viewModelScope.launch {
            cartRepository.updateQuantity(productId, currentQuantity - 1)
        }
    }

    fun removeItem(productId: Int) {
        viewModelScope.launch {
            cartRepository.remove(productId)
        }
    }

    fun placeOrder() {
        viewModelScope.launch {
            val items = uiState.value.items
            if (items.isEmpty()) return@launch
            items.forEach { item ->
                analyticsRepository.logEvent(InteractionType.COMPLETED_CHECKOUT, item.productId)
            }
            _snackbarMessage.value = "Interaction logged: COMPLETED CHECKOUT"
        }
    }

    fun consumeSnackbar() {
        _snackbarMessage.update { null }
    }
}
