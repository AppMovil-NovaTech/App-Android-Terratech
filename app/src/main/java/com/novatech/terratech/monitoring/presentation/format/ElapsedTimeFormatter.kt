package com.novatech.terratech.monitoring.presentation.format

import java.time.Duration
import java.time.Instant

object ElapsedTimeFormatter {
    fun elapsedMinutes(recordedAt: Instant, now: Instant): Int =
        Duration.between(recordedAt, now).toMinutes().coerceAtLeast(0).toInt()

    fun format(elapsedMinutes: Int): String {
        val safeMinutes = elapsedMinutes.coerceAtLeast(0)
        if (safeMinutes < 60) return "$safeMinutes min"
        val hours = safeMinutes / 60
        val remainingMinutes = safeMinutes % 60
        return if (remainingMinutes == 0) {
            "$hours h"
        } else {
            "$hours h $remainingMinutes min"
        }
    }
}
