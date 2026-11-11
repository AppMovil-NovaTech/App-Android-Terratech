package com.novatech.terratech.notifications.presentation.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.novatech.terratech.R
import com.novatech.terratech.core.presentation.component.FarmCard
import com.novatech.terratech.core.presentation.component.Pill
import com.novatech.terratech.notifications.domain.entity.NotificationPreferences
import com.novatech.terratech.notifications.presentation.component.PreferenceToggleRow
import com.novatech.terratech.notifications.presentation.state.NotificationPreferencesState

@Composable
fun NotificationPreferencesContent(
    state: NotificationPreferencesState,
    onPreferenceChange: ((NotificationPreferences) -> NotificationPreferences) -> Unit,
    modifier: Modifier = Modifier,
) {
    val prefs = state.preferences

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        state.message?.let {
            Pill(it)
        }

        FarmCard {
            Text(
                text = stringResource(R.string.notification_preferences_types),
                style = MaterialTheme.typography.titleMedium,
            )

            PreferenceToggleRow(
                title = stringResource(R.string.notification_pref_humidity),
                description = stringResource(R.string.notification_pref_humidity_desc),
                checked = prefs.enableHumidityAlerts,
                onCheckedChange = { checked ->
                    onPreferenceChange { it.copy(enableHumidityAlerts = checked) }
                },
                enabled = !state.busy,
            )

            PreferenceToggleRow(
                title = stringResource(R.string.notification_pref_nutrients),
                description = stringResource(R.string.notification_pref_nutrients_desc),
                checked = prefs.enableNutrientAlerts,
                onCheckedChange = { checked ->
                    onPreferenceChange { it.copy(enableNutrientAlerts = checked) }
                },
                enabled = !state.busy,
            )

            PreferenceToggleRow(
                title = stringResource(R.string.notification_pref_reviews),
                description = stringResource(R.string.notification_pref_reviews_desc),
                checked = prefs.enableReviewNotifications,
                onCheckedChange = { checked ->
                    onPreferenceChange { it.copy(enableReviewNotifications = checked) }
                },
                enabled = !state.busy,
            )

            PreferenceToggleRow(
                title = stringResource(R.string.notification_pref_system),
                description = stringResource(R.string.notification_pref_system_desc),
                checked = prefs.enableSystemUpdates,
                onCheckedChange = { checked ->
                    onPreferenceChange { it.copy(enableSystemUpdates = checked) }
                },
                enabled = !state.busy,
            )
        }

        FarmCard {
            Text(
                text = stringResource(R.string.notification_preferences_channels),
                style = MaterialTheme.typography.titleMedium,
            )

            PreferenceToggleRow(
                title = stringResource(R.string.notification_channel_app),
                description = "Alertas directas en la barra y panel de notificaciones.",
                checked = prefs.inAppChannel,
                onCheckedChange = { checked ->
                    onPreferenceChange { it.copy(inAppChannel = checked) }
                },
                enabled = !state.busy,
            )

            PreferenceToggleRow(
                title = stringResource(R.string.notification_channel_email),
                description = "Envío de resumen y alertas críticas a tu correo registrado.",
                checked = prefs.emailChannel,
                onCheckedChange = { checked ->
                    onPreferenceChange { it.copy(emailChannel = checked) }
                },
                enabled = !state.busy,
            )

            PreferenceToggleRow(
                title = stringResource(R.string.notification_channel_sms),
                description = "Alertas de emergencia por SMS a tu número de contacto.",
                checked = prefs.smsChannel,
                onCheckedChange = { checked ->
                    onPreferenceChange { it.copy(smsChannel = checked) }
                },
                enabled = !state.busy,
            )
        }
    }
}
