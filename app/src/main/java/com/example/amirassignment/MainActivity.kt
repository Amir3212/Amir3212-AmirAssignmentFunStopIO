package com.example.amirassignment

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.LaunchedEffect
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.example.amirassignment.ui.navigation.MarketplaceNavGraph
import com.example.amirassignment.ui.theme.AmirAssignmentTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private var keepSplashScreen = true

    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        splashScreen.setKeepOnScreenCondition { keepSplashScreen }
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            LaunchedEffect(Unit) {
                delay(SPLASH_DISPLAY_MS)
                keepSplashScreen = false
            }
            AmirAssignmentTheme {
                MarketplaceNavGraph()
            }
        }
    }

    private companion object {
        const val SPLASH_DISPLAY_MS = 1_500L
    }
}
