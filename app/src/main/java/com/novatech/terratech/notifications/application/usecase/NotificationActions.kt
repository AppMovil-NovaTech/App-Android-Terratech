package com.novatech.terratech.notifications.application.usecase

import com.novatech.terratech.notifications.domain.entity.NotificationItem
import com.novatech.terratech.notifications.domain.entity.NotificationPreferences
import com.novatech.terratech.notifications.domain.entity.NotificationType
import com.novatech.terratech.notifications.domain.repository.NotificationPreferencesRepository
import com.novatech.terratech.notifications.domain.repository.NotificationRepository

class NotificationActions(
    private val notificationRepository: NotificationRepository,
    private val preferencesRepository: NotificationPreferencesRepository,
) {
    suspend fun getNotifications(userId: Int, profileId: Int): List<NotificationItem> {
        val notifications = notificationRepository.getNotifications(userId, profileId)
        val preferences = preferencesRepository.getPreferences(userId)

        return notifications.filter { item ->
            when (item.type) {
                NotificationType.SENSOR_ALERT -> {
                    val isHumidity = item.title.contains("Humedad", ignoreCase = true) ||
                        item.message.contains("Humedad", ignoreCase = true)
                    if (isHumidity) {
                        preferences.enableHumidityAlerts
                    } else {
                        preferences.enableNutrientAlerts
                    }
                }
                NotificationType.REVIEW -> {
                    preferences.enableReviewNotifications
                }
                NotificationType.SYSTEM -> {
                    preferences.enableSystemUpdates
                }
            }
        }
    }

    suspend fun markAsRead(notificationId: Int): Boolean {
        return notificationRepository.markAsRead(notificationId)
    }

    suspend fun getPreferences(userId: Int): NotificationPreferences {
        return preferencesRepository.getPreferences(userId)
    }

    suspend fun savePreferences(preferences: NotificationPreferences): NotificationPreferences {
        return preferencesRepository.savePreferences(preferences)
    }
}
