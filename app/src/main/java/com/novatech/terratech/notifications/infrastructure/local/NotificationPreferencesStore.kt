package com.novatech.terratech.notifications.infrastructure.local

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.doublePreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import com.novatech.terratech.notifications.domain.entity.NotificationPreferences
import kotlinx.coroutines.flow.first

private val Context.notificationPreferencesData by preferencesDataStore("notification_preferences")

class NotificationPreferencesStore(private val context: Context) {

    suspend fun getPreferences(userId: Int): NotificationPreferences {
        val prefs = context.notificationPreferencesData.data.first()
        val humidityKey = booleanPreferencesKey("humidity_alerts_$userId")
        val nutrientKey = booleanPreferencesKey("nutrient_alerts_$userId")
        val reviewKey = booleanPreferencesKey("review_alerts_$userId")
        val systemKey = booleanPreferencesKey("system_updates_$userId")
        val inAppKey = booleanPreferencesKey("channel_in_app_$userId")
        val emailKey = booleanPreferencesKey("channel_email_$userId")
        val smsKey = booleanPreferencesKey("channel_sms_$userId")
        val thresholdKey = doublePreferencesKey("custom_moisture_threshold_$userId")

        return NotificationPreferences(
            userId = userId,
            enableHumidityAlerts = prefs[humidityKey] ?: true,
            enableNutrientAlerts = prefs[nutrientKey] ?: true,
            enableReviewNotifications = prefs[reviewKey] ?: true,
            enableSystemUpdates = prefs[systemKey] ?: true,
            inAppChannel = prefs[inAppKey] ?: true,
            emailChannel = prefs[emailKey] ?: true,
            smsChannel = prefs[smsKey] ?: false,
            customMoistureThreshold = prefs[thresholdKey],
        )
    }

    suspend fun savePreferences(preferences: NotificationPreferences): NotificationPreferences {
        val userId = preferences.userId
        val humidityKey = booleanPreferencesKey("humidity_alerts_$userId")
        val nutrientKey = booleanPreferencesKey("nutrient_alerts_$userId")
        val reviewKey = booleanPreferencesKey("review_alerts_$userId")
        val systemKey = booleanPreferencesKey("system_updates_$userId")
        val inAppKey = booleanPreferencesKey("channel_in_app_$userId")
        val emailKey = booleanPreferencesKey("channel_email_$userId")
        val smsKey = booleanPreferencesKey("channel_sms_$userId")
        val thresholdKey = doublePreferencesKey("custom_moisture_threshold_$userId")

        context.notificationPreferencesData.edit { prefs ->
            prefs[humidityKey] = preferences.enableHumidityAlerts
            prefs[nutrientKey] = preferences.enableNutrientAlerts
            prefs[reviewKey] = preferences.enableReviewNotifications
            prefs[systemKey] = preferences.enableSystemUpdates
            prefs[inAppKey] = preferences.inAppChannel
            prefs[emailKey] = preferences.emailChannel
            prefs[smsKey] = preferences.smsChannel
            if (preferences.customMoistureThreshold != null) {
                prefs[thresholdKey] = preferences.customMoistureThreshold
            } else {
                prefs.remove(thresholdKey)
            }
        }
        return preferences
    }
}
