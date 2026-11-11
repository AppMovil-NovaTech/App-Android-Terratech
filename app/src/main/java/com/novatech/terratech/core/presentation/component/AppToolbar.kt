package com.novatech.terratech.core.presentation.component

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.novatech.terratech.R
import com.novatech.terratech.core.presentation.navigation.AppDestination
import com.novatech.terratech.monitoring.presentation.state.MonitoringState
import com.novatech.terratech.profile.presentation.state.ProfileState

@Composable
fun AppToolbar(
    destination: AppDestination,
    monitoringState: MonitoringState,
    profileState: ProfileState,
    onBack: () -> Unit,
    onRefresh: () -> Unit,
    unreadNotificationsCount: Int = 0,
    onNavigateToNotifications: (() -> Unit)? = null,
) {
    if (!destination.showToolbar) return
    val title =
        when (destination) {
            AppDestination.SENSORS -> monitoringState.selectedField?.name.orEmpty()
            AppDestination.SENSOR,
            AppDestination.HISTORY,
            AppDestination.READING -> monitoringState.selectedSensor?.name.orEmpty()
            AppDestination.REGISTER_SENSOR -> stringResource(R.string.associate)
            AppDestination.INVENTORY,
            AppDestination.CREATE_INVENTORY,
            AppDestination.EDIT_INVENTORY -> stringResource(R.string.inventory_title)
            AppDestination.NOTIFICATIONS -> stringResource(R.string.notifications_title)
            AppDestination.NOTIFICATION_PREFERENCES -> stringResource(R.string.notifications_preferences)
            else -> profileState.profile?.fundoName ?: "TerraTech"
        }
    TerraTechToolbar(
        title = title,
        busy = monitoringState.busy,
        unreadNotificationsCount = unreadNotificationsCount,
        onBack = if (destination.isRoot) null else onBack,
        onRefresh = if (destination.canRefresh) onRefresh else null,
        onNotifications = if (destination != AppDestination.NOTIFICATIONS) onNavigateToNotifications else null,
    )
}
