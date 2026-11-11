package com.novatech.terratech.notifications.infrastructure.implementation

import com.novatech.terratech.notifications.domain.entity.NotificationPreferences
import com.novatech.terratech.notifications.domain.repository.NotificationPreferencesRepository
import com.novatech.terratech.notifications.infrastructure.local.NotificationPreferencesStore

class NotificationPreferencesRepositoryImpl(
    private val store: NotificationPreferencesStore,
) : NotificationPreferencesRepository {

    override suspend fun getPreferences(userId: Int): NotificationPreferences {
        return store.getPreferences(userId)
    }

    override suspend fun savePreferences(preferences: NotificationPreferences): NotificationPreferences {
        return store.savePreferences(preferences)
    }
}
