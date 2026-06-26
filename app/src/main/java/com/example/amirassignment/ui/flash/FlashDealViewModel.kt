package com.example.amirassignment.ui.flash

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.amirassignment.domain.flash.countdownFlow
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class FlashDealViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
) : ViewModel() {

    private val dealEndEpochMs: Long = savedStateHandle.get<Long>(KEY_END_EPOCH_MS)
        ?: (System.currentTimeMillis() + DEAL_DURATION_MS).also { end ->
            savedStateHandle[KEY_END_EPOCH_MS] = end
        }

    val uiState = countdownFlow(dealEndEpochMs)
        .map { tick ->
            FlashDealUiState(
                countdownText = tick.formatted,
                isExpired = tick.isExpired,
            )
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = FlashDealUiState(),
        )

    companion object {
        private const val KEY_END_EPOCH_MS = "deal_end_epoch_ms"
        private const val DEAL_DURATION_MS = 2 * 60 * 60 * 1000L
    }
}
