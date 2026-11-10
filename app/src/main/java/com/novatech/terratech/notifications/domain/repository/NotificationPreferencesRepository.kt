package com.novatech.terratech.notifications.domain.repository

import com.novatech.terratech.notifications.domain.entity.NotificationPreferences

interface NotificationPreferencesRepository {
    suspend fun getPreferences(userId: Int): NotificationPreferences
    suspend fun savePreferences(preferences: NotificationPreferences): NotificationPreferences
}
