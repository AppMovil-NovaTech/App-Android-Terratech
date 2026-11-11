package com.novatech.terratech.notifications.infrastructure.mapper

import com.novatech.terratech.notifications.domain.entity.NotificationItem
import com.novatech.terratech.notifications.domain.entity.NotificationType
import com.novatech.terratech.notifications.domain.valueobject.ReviewDetails
import com.novatech.terratech.notifications.infrastructure.remote.dto.NotificationDto

object NotificationMapper {
    fun toDomain(dto: NotificationDto): NotificationItem {
        val lowerTitle = dto.title.lowercase()
        val lowerMessage = dto.message.lowercase()

        val isReview = lowerTitle.contains("reseña") ||
            lowerTitle.contains("resena") ||
            lowerTitle.contains("review") ||
            lowerMessage.contains("estrellas") ||
            lowerMessage.contains("calificó") ||
            lowerMessage.contains("califico")

        val type = when {
            isReview -> NotificationType.REVIEW
            dto.isAlert || lowerTitle.contains("alerta") || lowerTitle.contains("humedad") || lowerTitle.contains("sensor") -> NotificationType.SENSOR_ALERT
            else -> NotificationType.SYSTEM
        }

        val reviewDetails = if (isReview) {
            parseReviewDetails(dto.title, dto.message)
        } else {
            null
        }

        return NotificationItem(
            id = dto.id,
            profileId = dto.profileId,
            title = dto.title,
            message = dto.message,
            isRead = dto.isRead,
            isAlert = dto.isAlert || (reviewDetails?.isCritical == true),
            type = type,
            timestamp = dto.createdAt ?: "Reciente",
            reviewDetails = reviewDetails,
        )
    }

    private fun parseReviewDetails(title: String, message: String): ReviewDetails {
        val combined = "$title $message".lowercase()
        val rating = when {
            combined.contains("1 estrella") || combined.contains("1⭐") -> 1
            combined.contains("2 estrella") || combined.contains("2⭐") -> 2
            combined.contains("3 estrella") || combined.contains("3⭐") -> 3
            combined.contains("4 estrella") || combined.contains("4⭐") -> 4
            combined.contains("5 estrella") || combined.contains("5⭐") -> 5
            combined.contains("negativa") || combined.contains("crítica") || combined.contains("critica") -> 2
            else -> 5
        }

        val isCritical = rating <= 2

        val author = when {
            title.contains(" de ") -> title.substringAfter(" de ").substringBefore(" sobre ").trim()
            message.contains(" por ") -> message.substringAfter(" por ").substringBefore(":").trim()
            else -> "Agricultor"
        }

        val product = when {
            title.contains(" sobre ") -> title.substringAfter(" sobre ").substringBefore(" -").trim()
            message.contains(" en ") -> message.substringAfter(" en ").substringBefore(".").trim()
            else -> "Fertilizante Orgánico"
        }

        return ReviewDetails(
            rating = rating,
            authorName = author,
            productName = product,
            commentText = message,
            isCritical = isCritical,
        )
    }
}
