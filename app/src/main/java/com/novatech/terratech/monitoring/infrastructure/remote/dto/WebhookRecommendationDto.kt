package com.novatech.terratech.monitoring.infrastructure.remote.dto

data class WebhookRecommendationDto(
    val id: Int,
    val fieldId: Int,
    val zone: String,
    val action: String,
    val durationMinutes: Int,
    val priority: String,
    val reason: String,
    val category: String,
    val deviceId: Int? = null,
    val fertilityZoneId: Int? = null,
    val source: String = "ANALYSIS_ENGINE",
    val externalId: String? = null,
    val createdAt: String,
)
