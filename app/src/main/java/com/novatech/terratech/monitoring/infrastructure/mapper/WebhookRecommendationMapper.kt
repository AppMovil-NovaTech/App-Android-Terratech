package com.novatech.terratech.monitoring.infrastructure.mapper

import com.novatech.terratech.monitoring.domain.valueobject.RecommendationSeverity
import com.novatech.terratech.monitoring.domain.valueobject.RecommendationType
import com.novatech.terratech.monitoring.infrastructure.local.entity.RecommendationRow
import com.novatech.terratech.monitoring.infrastructure.remote.dto.WebhookRecommendationDto
import java.time.Instant

object WebhookRecommendationMapper {
    fun toRow(
        dto: WebhookRecommendationDto,
        userId: Int,
        deviceId: Int,
        readingId: Int,
    ): RecommendationRow =
        RecommendationRow(
            userId = userId,
            deviceId = deviceId,
            readingId = readingId,
            type = RecommendationType.WEBHOOK.name,
            severity = mapSeverity(dto.priority).name,
            title = "${dto.zone}: ${dto.action}",
            message = "${dto.reason} (${dto.durationMinutes} min)",
            actionSuggested = "${dto.action} (${dto.durationMinutes} min)",
            createdAt = normalizeCreatedAt(dto.createdAt),
        )

    fun mapSeverity(priority: String): RecommendationSeverity =
        when (priority.trim().uppercase()) {
            "CRITICAL", "HIGH" -> RecommendationSeverity.CRITICAL
            "WARNING", "MEDIUM" -> RecommendationSeverity.WARNING
            else -> RecommendationSeverity.INFO
        }

    fun mapType(category: String): RecommendationType =
        when (category.trim().uppercase()) {
            "IRRIGATION" -> RecommendationType.WATERING
            "FERTILIZATION" -> RecommendationType.FERTILIZER
            "CLIMATE" -> RecommendationType.CLIMATE
            "GENERAL" -> RecommendationType.GENERAL
            else -> RecommendationType.WEBHOOK
        }

    private fun normalizeCreatedAt(value: String): String =
        runCatching { Instant.parse(value).toString() }
            .getOrElse { Instant.now().toString() }
}
