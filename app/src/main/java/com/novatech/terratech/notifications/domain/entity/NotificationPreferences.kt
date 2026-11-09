package com.novatech.terratech.notifications.domain.entity

data class NotificationPreferences(
    val userId: Int,
    val enableHumidityAlerts: Boolean = true,
    val enableNutrientAlerts: Boolean = true,
    val enableReviewNotifications: Boolean = true,
    val enableSystemUpdates: Boolean = true,
    val inAppChannel: Boolean = true,
    val emailChannel: Boolean = true,
    val smsChannel: Boolean = false,
    val customMoistureThreshold: Double? = null,
)
