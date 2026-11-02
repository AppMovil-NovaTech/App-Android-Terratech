package com.novatech.terratech.inventory.presentation.navigation

import androidx.compose.runtime.LaunchedEffect
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.novatech.terratech.core.presentation.navigation.AppNavigator
import com.novatech.terratech.inventory.presentation.state.InventoryState
import com.novatech.terratech.inventory.presentation.ui.CreateInventoryScreen

fun NavGraphBuilder.createInventoryDestination(
    navigator: AppNavigator,
    state: InventoryState,
    actions: InventoryNavigationActions,
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
            onRegisterExisting = actions.registerExisting,
            onRegisterNew = actions.registerNew,
            onCancel = navigator::back,
        )
    }
}
