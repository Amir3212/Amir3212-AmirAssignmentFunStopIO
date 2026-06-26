package com.example.amirassignment.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Category
import androidx.compose.material.icons.outlined.Checkroom
import androidx.compose.material.icons.outlined.DesktopWindows
import androidx.compose.material.icons.outlined.GridView
import androidx.compose.material.icons.outlined.ShoppingBag
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.amirassignment.ui.theme.MarketplaceAccent
import com.example.amirassignment.ui.theme.MarketplaceChipInactive
import com.example.amirassignment.ui.theme.MarketplaceNavy
import java.util.Locale

fun formatCategoryLabel(slug: String): String =
    when (slug) {
        "ALL" -> "All"
        else -> slug.replaceFirstChar { char ->
            if (char.isLowerCase()) char.titlecase(Locale.getDefault()) else char.toString()
        }
    }

private fun categoryIcon(slug: String): ImageVector {
    val lower = slug.lowercase(Locale.US)
    return when {
        slug == "ALL" -> Icons.Outlined.GridView
        lower.contains("grocery") || lower.contains("beauty") || lower.contains("fragrance") ->
            Icons.Outlined.ShoppingBag
        lower.contains("lifestyle") || lower.contains("furniture") || lower.contains("home") ->
            Icons.Outlined.Checkroom
        lower.contains("tech") || lower.contains("laptop") || lower.contains("phone") ||
            lower.contains("smartphone") || lower.contains("tablet") ->
            Icons.Outlined.DesktopWindows
        else -> Icons.Outlined.Category
    }
}

private fun Modifier.shimmerPlaceholder(): Modifier = composed {
    val transition = rememberInfiniteTransition(label = "shimmer")
    val translate by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1000f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1100, easing = LinearEasing),
            repeatMode = RepeatMode.Restart,
        ),
        label = "shimmerTranslate",
    )
    val base = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f)
    val highlight = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.16f)
    background(
        brush = Brush.linearGradient(
            colors = listOf(base, highlight, base),
            start = Offset(translate - 200f, 0f),
            end = Offset(translate, 0f),
        ),
    )
}

@Composable
fun CategoryFilterRow(
    categories: List<String>,
    selected: String,
    onSelected: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyRow(
        modifier = modifier.fillMaxWidth(),
        contentPadding = PaddingValues(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        items(categories, key = { it }) { category ->
            val selectedChip = category == selected
            val containerColor by animateColorAsState(
                if (selectedChip) MarketplaceNavy else MarketplaceChipInactive,
                label = "chipColor",
            )
            val contentColor = if (selectedChip) Color.White else MarketplaceNavy
            val accentChip = !selectedChip && isAccentCategory(category)

            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(24.dp))
                    .background(if (accentChip) MarketplaceAccent else containerColor)
                    .clickable { onSelected(category) }
                    .padding(horizontal = 16.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp),
            ) {
                Icon(
                    imageVector = categoryIcon(category),
                    contentDescription = null,
                    tint = if (accentChip) MarketplaceNavy else contentColor,
                    modifier = Modifier.size(18.dp),
                )
                Text(
                    text = formatCategoryLabel(category),
                    color = if (accentChip) MarketplaceNavy else contentColor,
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = if (selectedChip) FontWeight.SemiBold else FontWeight.Medium,
                )
            }
        }
    }
}

private fun isAccentCategory(slug: String): Boolean {
    val lower = slug.lowercase(Locale.US)
    return lower.contains("tech") || lower.contains("laptop") || lower.contains("phone")
}

@Composable
fun ProductCardShimmer(modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(Color.White)
            .padding(12.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Box(
            modifier = Modifier
                .size(100.dp)
                .clip(RoundedCornerShape(12.dp))
                .shimmerPlaceholder(),
        )
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.4f)
                    .height(14.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .shimmerPlaceholder(),
            )
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(16.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .shimmerPlaceholder(),
            )
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.5f)
                    .height(20.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .shimmerPlaceholder(),
            )
        }
    }
}
