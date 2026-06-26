package com.example.amirassignment.ui.feed



import android.widget.Toast

import androidx.compose.foundation.background

import androidx.compose.foundation.layout.Box

import androidx.compose.foundation.layout.Row

import androidx.compose.foundation.layout.Spacer

import androidx.compose.foundation.layout.WindowInsets

import androidx.compose.foundation.layout.fillMaxSize

import androidx.compose.foundation.layout.fillMaxWidth

import androidx.compose.foundation.layout.padding

import androidx.compose.foundation.layout.size

import androidx.compose.foundation.layout.statusBarsPadding

import androidx.compose.foundation.lazy.LazyColumn

import androidx.compose.foundation.lazy.rememberLazyListState

import androidx.compose.foundation.shape.CircleShape

import androidx.compose.material.icons.Icons

import androidx.compose.material.icons.outlined.Notifications

import androidx.compose.material.icons.outlined.Search

import androidx.compose.material3.CircularProgressIndicator

import androidx.compose.material3.ExperimentalMaterial3Api

import androidx.compose.material3.Icon

import androidx.compose.material3.IconButton

import androidx.compose.material3.MaterialTheme

import androidx.compose.material3.Scaffold

import androidx.compose.material3.SnackbarHost

import androidx.compose.material3.SnackbarHostState

import androidx.compose.material3.Text

import androidx.compose.material3.pulltorefresh.PullToRefreshBox

import androidx.compose.runtime.Composable

import androidx.compose.runtime.LaunchedEffect

import androidx.compose.runtime.derivedStateOf

import androidx.compose.runtime.getValue

import androidx.compose.runtime.remember

import androidx.compose.ui.Alignment

import androidx.compose.ui.Modifier

import androidx.compose.ui.draw.clip

import androidx.compose.ui.platform.LocalContext

import androidx.compose.ui.text.font.FontWeight

import androidx.compose.ui.unit.dp

import androidx.hilt.navigation.compose.hiltViewModel

import androidx.lifecycle.compose.collectAsStateWithLifecycle

import androidx.paging.LoadState

import androidx.paging.compose.LazyPagingItems

import androidx.paging.compose.collectAsLazyPagingItems

import androidx.paging.compose.itemContentType

import androidx.paging.compose.itemKey

import com.example.amirassignment.domain.model.InteractionType

import com.example.amirassignment.domain.model.Product

import com.example.amirassignment.ui.components.CategoryFilterRow

import com.example.amirassignment.ui.components.ProductCard

import com.example.amirassignment.ui.components.ProductCardShimmer

import com.example.amirassignment.ui.flash.FlashDealSection

import com.example.amirassignment.ui.theme.MarketplaceChipInactive

import com.example.amirassignment.ui.theme.MarketplaceNavy



private const val CATEGORY_FILTER_LIST_INDEX = 2



private const val CONTENT_TYPE_HEADER = "header"

private const val CONTENT_TYPE_FLASH = "flash"

private const val CONTENT_TYPE_CATEGORY = "category"

private const val CONTENT_TYPE_SHIMMER = "shimmer"

private const val CONTENT_TYPE_EMPTY = "empty"

private const val CONTENT_TYPE_PRODUCT = "product"

private const val CONTENT_TYPE_APPEND = "append"



@OptIn(ExperimentalMaterial3Api::class)

@Composable

fun ProductFeedScreen(

    viewModel: ProductFeedViewModel = hiltViewModel(),

) {

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    val pagingItems = viewModel.products.collectAsLazyPagingItems()

    val snackbarHostState = remember { SnackbarHostState() }

    val actions = remember(viewModel) { ProductFeedActions(viewModel) }



    ProductFeedEffects(

        pagingItems = pagingItems,

        snackbarMessage = uiState.snackbarMessage,

        snackbarHostState = snackbarHostState,

        onPagingError = viewModel::onPagingError,

        onConsumeSnackbar = viewModel::consumeSnackbar,

    )



    Scaffold(

        snackbarHost = { SnackbarHost(snackbarHostState) },

        containerColor = MaterialTheme.colorScheme.background,

        contentWindowInsets = WindowInsets(0, 0, 0, 0),

    ) { padding ->

        PullToRefreshBox(

            isRefreshing = uiState.isRefreshing,

            onRefresh = actions.refresh,

            modifier = Modifier

                .fillMaxSize()

                .padding(bottom = padding.calculateBottomPadding()),

        ) {

            ProductFeedList(

                pagingItems = pagingItems,

                categories = uiState.categories,

                selectedCategory = uiState.selectedCategory,

                cartProductIds = uiState.cartProductIds,

                joinedPoolProductIds = uiState.joinedPoolProductIds,

                expandedProductId = uiState.expandedProductId,

                flashPoolJoined = uiState.flashPoolJoined,

                showShimmer = uiState.showShimmer,

                errorMessage = uiState.errorMessage,

                actions = actions,

            )

        }

    }

}



private class ProductFeedActions(viewModel: ProductFeedViewModel) {

    val selectCategory = viewModel::selectCategory

    val refresh = viewModel::refresh

    val joinFlashPool: () -> Unit = { viewModel.joinPool(productId = null) }

    val toggleExpanded: (Int) -> Unit = viewModel::toggleProductExpanded

    val logInteraction: (InteractionType, Int) -> Unit = { type, id ->

        viewModel.logInteraction(type, id)

    }

    val addToCart = viewModel::addToCart

}



@Composable

private fun ProductFeedEffects(

    pagingItems: LazyPagingItems<Product>,

    snackbarMessage: String?,

    snackbarHostState: SnackbarHostState,

    onPagingError: (String?) -> Unit,

    onConsumeSnackbar: () -> Unit,

) {

    val context = LocalContext.current



    LaunchedEffect(pagingItems.loadState.refresh) {

        when (val state = pagingItems.loadState.refresh) {

            is LoadState.Error -> onPagingError(state.error.message)

            is LoadState.NotLoading -> onPagingError(null)

            else -> Unit

        }

    }



    LaunchedEffect(snackbarMessage) {

        snackbarMessage?.let { message ->

            if (message == "Pool joined...") {

                Toast.makeText(context, message, Toast.LENGTH_SHORT).show()

            } else {

                snackbarHostState.showSnackbar(message)

            }

            onConsumeSnackbar()

        }

    }

}



@Composable

private fun ProductFeedList(

    pagingItems: LazyPagingItems<Product>,

    categories: List<String>,

    selectedCategory: String,

    cartProductIds: Set<Int>,

    joinedPoolProductIds: Set<Int>,

    expandedProductId: Int?,

    flashPoolJoined: Boolean,

    showShimmer: Boolean,

    errorMessage: String?,

    actions: ProductFeedActions,

) {

    val listState = rememberLazyListState()

    val stickCategoryFilters by remember {

        derivedStateOf {

            val index = listState.firstVisibleItemIndex

            val offset = listState.firstVisibleItemScrollOffset

            index > CATEGORY_FILTER_LIST_INDEX ||

                (index == CATEGORY_FILTER_LIST_INDEX && offset > 0)

        }

    }

    val showAppendLoader = pagingItems.loadState.append is LoadState.Loading



    Box(modifier = Modifier.fillMaxSize()) {

        LazyColumn(

            state = listState,

            modifier = Modifier.fillMaxSize(),

        ) {

            item(contentType = CONTENT_TYPE_HEADER) {

                MarketplaceHeader(

                    modifier = Modifier

                        .statusBarsPadding()

                        .padding(horizontal = 16.dp, vertical = 8.dp),

                )

            }

            item(contentType = CONTENT_TYPE_FLASH) {

                FlashDealSection(

                    poolJoined = flashPoolJoined,

                    onJoinPool = actions.joinFlashPool,

                )

            }

            item(key = "category_filters", contentType = CONTENT_TYPE_CATEGORY) {

                CategoryFilterRow(

                    categories = categories,

                    selected = selectedCategory,

                    onSelected = actions.selectCategory,

                    modifier = Modifier

                        .fillMaxWidth()

                        .padding(vertical = 8.dp),

                )

            }

            if (showShimmer && pagingItems.itemCount == 0) {

                items(4, contentType = { CONTENT_TYPE_SHIMMER }) {

                    ProductCardShimmer()

                }

            } else if (pagingItems.itemCount == 0 && pagingItems.loadState.refresh is LoadState.NotLoading) {

                item(contentType = CONTENT_TYPE_EMPTY) {

                    Box(

                        modifier = Modifier

                            .fillMaxWidth()

                            .padding(32.dp),

                        contentAlignment = Alignment.Center,

                    ) {

                        Text(

                            text = errorMessage ?: "No products in this category.",

                            style = MaterialTheme.typography.bodyLarge,

                        )

                    }

                }

            } else {

                items(

                    count = pagingItems.itemCount,

                    key = pagingItems.itemKey { it.id },

                    contentType = pagingItems.itemContentType { CONTENT_TYPE_PRODUCT },

                ) { index ->

                    val product = pagingItems[index] ?: return@items

                    FeedProductRow(

                        product = product,

                        isInCart = product.id in cartProductIds,

                        isPoolJoined = product.id in joinedPoolProductIds,

                        isExpanded = product.id == expandedProductId,

                        onToggleExpand = { actions.toggleExpanded(product.id) },

                        onInteraction = actions.logInteraction,

                        onAddToCart = actions.addToCart,

                    )

                }

            }

            if (showAppendLoader) {

                item(contentType = CONTENT_TYPE_APPEND) {

                    Box(

                        modifier = Modifier

                            .fillMaxWidth()

                            .padding(16.dp),

                        contentAlignment = Alignment.Center,

                    ) {

                        CircularProgressIndicator()

                    }

                }

            }

        }

        if (stickCategoryFilters) {

            StickyCategoryFilterBar(

                categories = categories,

                selectedCategory = selectedCategory,

                onSelected = actions.selectCategory,

                modifier = Modifier.align(Alignment.TopCenter),

            )

        }

    }

}



@Composable

private fun StickyCategoryFilterBar(

    categories: List<String>,

    selectedCategory: String,

    onSelected: (String) -> Unit,

    modifier: Modifier = Modifier,

) {

    CategoryFilterRow(

        categories = categories,

        selected = selectedCategory,

        onSelected = onSelected,

        modifier = modifier

            .fillMaxWidth()

            .statusBarsPadding()

            .background(MaterialTheme.colorScheme.background)

            .padding(vertical = 8.dp),

    )

}



@Composable

private fun FeedProductRow(

    product: Product,

    isInCart: Boolean,

    isPoolJoined: Boolean,

    isExpanded: Boolean,

    onToggleExpand: () -> Unit,

    onInteraction: (InteractionType, Int) -> Unit,

    onAddToCart: (Product) -> Unit,

) {

    ProductCard(

        product = product,

        isInCart = isInCart,

        isPoolJoined = isPoolJoined,

        isExpanded = isExpanded,

        onToggleExpand = onToggleExpand,

        onInteraction = onInteraction,

        onAddToCart = onAddToCart,

    )

}



@Composable

private fun MarketplaceHeader(modifier: Modifier = Modifier) {

    Row(

        modifier = modifier.fillMaxWidth(),

        verticalAlignment = Alignment.CenterVertically,

    ) {

        Text(

            text = "Marketplace",

            style = MaterialTheme.typography.headlineMedium,

            fontWeight = FontWeight.Bold,

            color = MarketplaceNavy,

            modifier = Modifier.weight(1f),

        )

        IconButton(

            onClick = { },

            modifier = Modifier

                .size(40.dp)

                .clip(CircleShape)

                .background(MarketplaceChipInactive),

        ) {

            Icon(

                imageVector = Icons.Outlined.Search,

                contentDescription = "Search",

                tint = MarketplaceNavy,

            )

        }

        Spacer(modifier = Modifier.size(16.dp))

        Box {

            IconButton(

                onClick = { },

                modifier = Modifier

                    .size(40.dp)

                    .clip(CircleShape)

                    .background(MarketplaceChipInactive),

            ) {

                Icon(

                    imageVector = Icons.Outlined.Notifications,

                    contentDescription = "Notifications",

                    tint = MarketplaceNavy,

                )

            }

            Box(

                modifier = Modifier

                    .align(Alignment.TopEnd)

                    .padding(8.dp)

                    .size(8.dp)

                    .clip(CircleShape)

                    .background(MaterialTheme.colorScheme.error),

            )

        }

    }

}

