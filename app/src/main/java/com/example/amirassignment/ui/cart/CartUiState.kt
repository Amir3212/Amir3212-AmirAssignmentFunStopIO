package com.example.amirassignment.ui.cart

data class CartUiState(
    val items: List<com.example.amirassignment.domain.model.CartItem> = emptyList(),
    val isEmpty: Boolean = true,
)
