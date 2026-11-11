package com.novatech.terratech.notifications.presentation.state

import com.novatech.terratech.notifications.domain.entity.NotificationPreferences

data class NotificationPreferencesState(
    val userId: Int = 0,
    val preferences: NotificationPreferences = NotificationPreferences(userId = 0),
    val busy: Boolean = false,
    val message: String? = null,
    val error: String? = null,
)
