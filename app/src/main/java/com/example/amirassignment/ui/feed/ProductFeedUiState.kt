package com.example.amirassignment.ui.feed



data class ProductFeedUiState(

    val selectedCategory: String = "ALL",

    val categories: List<String> = listOf("ALL"),

    val cartItemCount: Int = 0,

    val cartProductIds: Set<Int> = emptySet(),

    val isRefreshing: Boolean = false,

    val showShimmer: Boolean = true,

    val errorMessage: String? = null,

    val snackbarMessage: String? = null,

    val joinedPoolProductIds: Set<Int> = emptySet(),

    val flashPoolJoined: Boolean = false,

    val expandedProductId: Int? = null,

)

