package com.example.amirassignment.ui.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.outlined.ShoppingBag
import androidx.compose.material.icons.outlined.ShoppingCart
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.example.amirassignment.ui.theme.MarketplaceNavy

@Composable
fun MarketplaceBottomBar(
    selectedRoute: String,
    cartItemCount: Int,
    onMarketplaceSelected: () -> Unit,
    onCartSelected: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .navigationBarsPadding()
            .height(64.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        BottomNavItem(
            label = "Marketplace",
            selected = selectedRoute == MarketplaceRoutes.FEED,
            selectedIcon = Icons.Outlined.ShoppingBag,
            unselectedIcon = Icons.Outlined.ShoppingBag,
            onClick = onMarketplaceSelected,
        )
        BottomNavItem(
            label = "Cart",
            selected = selectedRoute == MarketplaceRoutes.CART,
            selectedIcon = Icons.Filled.ShoppingCart,
            unselectedIcon = Icons.Outlined.ShoppingCart,
            onClick = onCartSelected,
            badgeCount = cartItemCount,
        )
    }
}

@Composable
private fun BottomNavItem(
    label: String,
    selected: Boolean,
    selectedIcon: ImageVector,
    unselectedIcon: ImageVector,
    onClick: () -> Unit,
    badgeCount: Int = 0,
) {
    val tint = if (selected) MarketplaceNavy else MaterialTheme.colorScheme.onSurfaceVariant
    val iconSize = if (selected) 28.dp else 22.dp

    Column(
        modifier = Modifier
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick,
            )
            .padding(horizontal = 24.dp, vertical = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(2.dp),
    ) {
        if (badgeCount > 0) {
            BadgedBox(
                badge = { Badge { Text(badgeCount.toString()) } },
            ) {
                Icon(
                    imageVector = if (selected) selectedIcon else unselectedIcon,
                    contentDescription = label,
                    tint = tint,
                    modifier = Modifier.size(iconSize),
                )
            }
        } else {
            Icon(
                imageVector = if (selected) selectedIcon else unselectedIcon,
                contentDescription = label,
                tint = tint,
                modifier = Modifier.size(iconSize),
            )
        }
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = tint,
        )
    }
}
