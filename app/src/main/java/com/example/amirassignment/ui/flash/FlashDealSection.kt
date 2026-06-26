package com.example.amirassignment.ui.flash

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.amirassignment.ui.theme.FlashBoltYellow
import com.example.amirassignment.ui.theme.MarketplaceNavy

@Composable
fun FlashDealSection(
    onJoinPool: () -> Unit,
    poolJoined: Boolean = false,
    modifier: Modifier = Modifier,
    viewModel: FlashDealViewModel = hiltViewModel(),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val (hours, minutes, seconds) = parseCountdown(state.countdownText)

    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(MarketplaceNavy)
            .padding(20.dp),
    ) {
        Icon(
            imageVector = Icons.Default.Bolt,
            contentDescription = null,
            tint = FlashBoltYellow,
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .size(96.dp)
                .padding(end = 8.dp),
        )
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .background(Color(0xFF243656))
                    .padding(horizontal = 12.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                Icon(
                    imageVector = Icons.Default.Bolt,
                    contentDescription = null,
                    tint = FlashBoltYellow,
                    modifier = Modifier.size(14.dp),
                )
                Text(
                    text = "FLASH POOL DEAL",
                    color = Color.White,
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.SemiBold,
                    letterSpacing = 0.5.sp,
                )
            }
            if (state.isExpired) {
                Text(
                    text = "Expired",
                    color = Color.White,
                    style = MaterialTheme.typography.displaySmall,
                    fontWeight = FontWeight.Bold,
                )
            } else {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalAlignment = Alignment.Bottom,
                ) {
                    CountdownUnit(value = hours, label = "HRS")
                    CountdownSeparator()
                    CountdownUnit(value = minutes, label = "MINS")
                    CountdownSeparator()
                    CountdownUnit(value = seconds, label = "SECS")
                }
            }
            Button(
                onClick = onJoinPool,
                enabled = !state.isExpired && !poolJoined,
                shape = RoundedCornerShape(24.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.White,
                    contentColor = MarketplaceNavy,
                    disabledContainerColor = Color.White.copy(alpha = 0.5f),
                ),
                modifier = Modifier.padding(top = 4.dp),
            ) {
                Text(
                    text = if (poolJoined) "Joined" else "Join Pool →",
                    fontWeight = FontWeight.SemiBold,
                )
            }
        }
    }
}

@Composable
private fun CountdownUnit(value: String, label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = value,
            color = Color.White,
            style = MaterialTheme.typography.displaySmall,
            fontWeight = FontWeight.Bold,
        )
        Text(
            text = label,
            color = Color.White.copy(alpha = 0.7f),
            style = MaterialTheme.typography.labelSmall,
        )
    }
}

@Composable
private fun CountdownSeparator() {
    Text(
        text = ":",
        color = Color.White,
        style = MaterialTheme.typography.displaySmall,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(bottom = 16.dp),
    )
}

private fun parseCountdown(countdownText: String): Triple<String, String, String> {
    val parts = countdownText.split(":")
    return when (parts.size) {
        3 -> Triple(parts[0], parts[1], parts[2])
        else -> Triple("--", "--", "--")
    }
}
