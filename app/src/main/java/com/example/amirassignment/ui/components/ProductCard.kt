package com.example.amirassignment.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.AddShoppingCart
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedIconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.imageLoader
import coil.request.ImageRequest
import com.example.amirassignment.domain.model.InteractionType
import com.example.amirassignment.domain.model.Product
import com.example.amirassignment.ui.theme.MarketplaceAccent
import com.example.amirassignment.ui.theme.MarketplaceChipInactive
import com.example.amirassignment.ui.theme.MarketplaceNavy
import com.example.amirassignment.ui.theme.StockGreen
import com.example.amirassignment.ui.theme.StockGreenContainer
import java.util.Locale

@Composable
fun ProductCard(
    product: Product,
    isInCart: Boolean,
    isPoolJoined: Boolean,
    isExpanded: Boolean,
    onToggleExpand: () -> Unit,
    onInteraction: (InteractionType, Int) -> Unit,
    onAddToCart: (Product) -> Unit,
    modifier: Modifier = Modifier,
) {
    var wishlisted by remember(product.id) { mutableStateOf(false) }
    val rating = 4.0 + (product.id % 10) * 0.1
    val reviewCount = 50 + product.id * 3
    val context = LocalContext.current
    val imageLoader = context.imageLoader
    val thumbnailSizePx = with(LocalDensity.current) { 100.dp.roundToPx() }
    val imageRequest = remember(product.id, product.imageUrl, thumbnailSizePx) {
        ImageRequest.Builder(context)
            .data(product.imageUrl)
            .size(thumbnailSizePx)
            .crossfade(false)
            .build()
    }

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .border(1.dp, MarketplaceChipInactive, RoundedCornerShape(16.dp)),
        shape = RoundedCornerShape(16.dp),
        color = Color.White,
        shadowElevation = 0.dp,
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(onClick = onToggleExpand),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                Column(
                    modifier = Modifier.width(100.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Box {
                        AsyncImage(
                            model = imageRequest,
                            imageLoader = imageLoader,
                            contentDescription = product.title,
                            modifier = Modifier
                                .size(100.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(MarketplaceChipInactive),
                            contentScale = ContentScale.Crop,
                        )
                        IconButton(
                            onClick = { wishlisted = !wishlisted },
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .size(32.dp),
                        ) {
                            Icon(
                                imageVector = if (wishlisted) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                                contentDescription = "Wishlist",
                                tint = if (wishlisted) Color(0xFFE53935) else MarketplaceNavy,
                                modifier = Modifier.size(20.dp),
                            )
                        }
                    }
                    Text(
                        text = "In stock: ${product.inventoryCount}",
                        modifier = Modifier
                            .padding(top = 6.dp)
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(StockGreenContainer)
                            .padding(horizontal = 8.dp, vertical = 4.dp),
                        style = MaterialTheme.typography.labelSmall,
                        color = StockGreen,
                        fontWeight = FontWeight.Medium,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = product.title,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = MarketplaceNavy,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                    )
                    Row(
                        modifier = Modifier.padding(top = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                    ) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = null,
                            tint = Color(0xFFFFB300),
                            modifier = Modifier.size(16.dp),
                        )
                        Text(
                            text = "%.1f | %d reviews".format(Locale.US, rating, reviewCount),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                    Row(
                        modifier = Modifier.padding(top = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        Text(
                            text = "$%.2f".format(Locale.US, product.price),
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = MarketplaceNavy,
                        )
                        if (product.discountPercentage > 0) {
                            Text(
                                text = "%.0f%% off".format(Locale.US, product.discountPercentage),
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.SemiBold,
                                color = Color(0xFFE53935),
                            )
                        }
                    }
                    if (product.brand.isNotBlank()) {
                        Text(
                            text = product.brand,
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(top = 4.dp),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                    }
                    Text(
                        text = formatCategoryLabel(product.category),
                        style = MaterialTheme.typography.labelSmall,
                        color = MarketplaceNavy,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier
                            .padding(top = 4.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(MarketplaceAccent)
                            .padding(horizontal = 8.dp, vertical = 4.dp),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
            }
            AnimatedVisibility(
                visible = isExpanded && product.description.isNotBlank(),
                enter = expandVertically() + fadeIn(),
                exit = shrinkVertically() + fadeOut(),
            ) {
                Text(
                    text = product.description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(top = 8.dp),
                )
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Button(
                    onClick = {
                        if (!isPoolJoined) {
                            onInteraction(InteractionType.JOIN_POOL, product.id)
                        }
                    },
                    enabled = !isPoolJoined,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MarketplaceNavy,
                        contentColor = Color.White,
                        disabledContainerColor = MarketplaceChipInactive,
                        disabledContentColor = MarketplaceNavy,
                    ),
                ) {
                    Icon(
                        imageVector = Icons.Default.Groups,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp),
                    )
                    Text(
                        text = if (isPoolJoined) "Joined" else "Join Pool",
                        modifier = Modifier.padding(start = 4.dp),
                        fontWeight = FontWeight.SemiBold,
                    )
                }
                OutlinedIconButton(
                    onClick = { onInteraction(InteractionType.SHARED_LINK, product.id) },
                    modifier = Modifier
                        .size(44.dp)
                        .border(1.dp, MarketplaceChipInactive, CircleShape),
                    shape = CircleShape,
                ) {
                    Icon(
                        imageVector = Icons.Default.Share,
                        contentDescription = "Share",
                        tint = MarketplaceNavy,
                    )
                }
                OutlinedIconButton(
                    onClick = { onAddToCart(product) },
                    modifier = Modifier
                        .size(44.dp)
                        .border(1.dp, MarketplaceChipInactive, CircleShape),
                    shape = CircleShape,
                ) {
                    Icon(
                        imageVector = if (isInCart) {
                            Icons.Default.ShoppingCart
                        } else {
                            Icons.Outlined.AddShoppingCart
                        },
                        contentDescription = if (isInCart) "In cart" else "Add to cart",
                        tint = if (isInCart) StockGreen else MarketplaceNavy,
                    )
                }
            }
        }
    }
}
