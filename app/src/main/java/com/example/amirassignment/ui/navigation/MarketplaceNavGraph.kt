package com.example.amirassignment.ui.navigation

import androidx.activity.ComponentActivity
import androidx.activity.compose.LocalActivity
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.amirassignment.ui.cart.CartScreen
import com.example.amirassignment.ui.cart.CartViewModel
import com.example.amirassignment.ui.feed.ProductFeedScreen

object MarketplaceRoutes {
    const val FEED = "feed"
    const val CART = "cart"
}

@Composable
fun MarketplaceNavGraph(
    navController: NavHostController = rememberNavController(),
) {
    val activity = LocalActivity.current as ComponentActivity
    val cartViewModel: CartViewModel = hiltViewModel(activity)
    val cartItemCount by cartViewModel.itemCount.collectAsStateWithLifecycle()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route ?: MarketplaceRoutes.FEED

    fun navigateToTab(route: String) {
        navController.navigate(route) {
            popUpTo(MarketplaceRoutes.FEED) {
                saveState = true
            }
            launchSingleTop = true
            restoreState = true
        }
    }

    Scaffold(
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        bottomBar = {
            MarketplaceBottomBar(
                selectedRoute = currentRoute,
                cartItemCount = cartItemCount,
                onMarketplaceSelected = { navigateToTab(MarketplaceRoutes.FEED) },
                onCartSelected = { navigateToTab(MarketplaceRoutes.CART) },
            )
        },
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = MarketplaceRoutes.FEED,
            modifier = Modifier.padding(bottom = innerPadding.calculateBottomPadding()),
        ) {
            composable(MarketplaceRoutes.FEED) {
                ProductFeedScreen()
            }
            composable(MarketplaceRoutes.CART) {
                CartScreen(
                    onNavigateBack = { navigateToTab(MarketplaceRoutes.FEED) },
                    onBrowseProducts = { navigateToTab(MarketplaceRoutes.FEED) },
                    showBackNavigation = false,
                    viewModel = cartViewModel,
                )
            }
        }
    }
}
