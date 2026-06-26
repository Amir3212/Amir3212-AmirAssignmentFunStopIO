package com.example.amirassignment.domain.flash

import org.junit.Assert.assertEquals
import org.junit.Test

class FlashCountdownTest {

    @Test
    fun formatRemaining_formatsHms() {
        assertEquals("01:02:03", formatRemaining(3_723_000))
        assertEquals("00:00:00", formatRemaining(0))
    }
}
