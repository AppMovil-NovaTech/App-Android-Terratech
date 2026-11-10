package com.novatech.terratech.core.presentation.ui

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import com.novatech.terratech.core.presentation.navigation.AppNavigator
import com.novatech.terratech.core.presentation.navigation.HomeGraph
import com.novatech.terratech.core.presentation.navigation.homeGraph
import com.novatech.terratech.iam.domain.entity.Session
import com.novatech.terratech.inventory.presentation.navigation.InventoryNavigationActions
import com.novatech.terratech.inventory.presentation.navigation.inventoryGraph
import com.novatech.terratech.inventory.presentation.state.InventoryState
import com.novatech.terratech.monitoring.presentation.navigation.MonitoringNavigationActions
import com.novatech.terratech.monitoring.presentation.navigation.monitoringGraph
import com.novatech.terratech.monitoring.presentation.state.MonitoringState
import com.novatech.terratech.notifications.presentation.navigation.notificationsGraph
import com.novatech.terratech.notifications.presentation.viewmodel.NotificationPreferencesViewModel
import com.novatech.terratech.notifications.presentation.viewmodel.NotificationsViewModel
import com.novatech.terratech.profile.presentation.navigation.ProfileNavigationActions
import com.novatech.terratech.profile.presentation.navigation.profileGraph
import com.novatech.terratech.profile.presentation.state.ProfileState

@Composable
internal fun TerraTechNavHost(
    navController: NavHostController,
    navigator: AppNavigator,
    session: Session,
    profileState: ProfileState,
    monitoringState: MonitoringState,
    monitoringActions: MonitoringNavigationActions,
    inventoryState: InventoryState,
    inventoryActions: InventoryNavigationActions,
    profileActions: ProfileNavigationActions,
    notificationsViewModel: NotificationsViewModel,
    preferencesViewModel: NotificationPreferencesViewModel,
) {
    NavHost(navController = navController, startDestination = HomeGraph) {
        homeGraph(navigator, session, profileState, monitoringState)
        monitoringGraph(navigator, monitoringState, profileState, monitoringActions)
        inventoryGraph(
            navigator,
            inventoryState,
            inventoryActions,
            profileState.profile?.id,
            monitoringState.fields,
        )
        profileGraph(
            navigator,
            session,
            profileState,
            profileActions,
            monitoringState.fields.isNotEmpty(),
        )
        notificationsGraph(
            navigator,
            session,
            profileState,
            notificationsViewModel,
            preferencesViewModel,
        )
    }
}
