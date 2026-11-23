package com.novatech.terratech.monitoring.infrastructure.remote.dto

data class CreateDeviceDto(
    val fieldId: Int,
    val macAddress: String,
    val status: String,
    val lastSync: String,
    val sensorCode: String? = null,
    val name: String? = null,
    val batteryLevelPercent: Int? = null,
)