package com.novatech.terratech.monitoring.infrastructure.remote.dto

data class LatestDto(
    val reading: ReadingDto,
    val isStale: Boolean,
    val fieldId: Int? = null,
    val sensorCode: String? = null,
    val batteryLevelPercent: Int? = null,
    val elapsedMinutes: Int? = null,
)
