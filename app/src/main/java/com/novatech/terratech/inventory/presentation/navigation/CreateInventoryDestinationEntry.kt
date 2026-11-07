package com.novatech.terratech.inventory.presentation.navigation

import androidx.compose.runtime.LaunchedEffect
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.novatech.terratech.core.presentation.navigation.AppNavigator
import com.novatech.terratech.inventory.presentation.state.InventoryState
import com.novatech.terratech.inventory.presentation.ui.CreateInventoryScreen
import com.novatech.terratech.monitoring.domain.entity.Field

fun NavGraphBuilder.createInventoryDestination(
    navigator: AppNavigator,
    state: InventoryState,
    actions: InventoryNavigationActions,
    profileId: Int?,
    fields: List<Field>,
) {
    composable<CreateInventoryDestination> {
        LaunchedEffect(Unit) { actions.clearMessage() }
        LaunchedEffect(state.saved) {
            if (state.saved) {
                actions.clearMessage()
                navigator.back()
            }
        }
        CreateInventoryScreen(
            state = state,
            profileId = profileId,
            fields = fields,
            onRegister = actions.register,
            onCancel = navigator::back,
        )
    }
}
