package com.novatech.terratech.inventory.presentation.navigation

import androidx.compose.runtime.LaunchedEffect
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.novatech.terratech.core.presentation.navigation.AppNavigator
import com.novatech.terratech.inventory.presentation.state.InventoryState
import com.novatech.terratech.inventory.presentation.ui.InventoryScreen

fun NavGraphBuilder.inventoryDestination(
    navigator: AppNavigator,
    state: InventoryState,
    actions: InventoryNavigationActions,
) {
    composable<InventoryDestination> {
        LaunchedEffect(Unit) { actions.refresh() }
        InventoryScreen(
            state = state,
            onCreate = navigator::createInventory,
            onEdit = navigator::editInventory,
            onRefresh = actions.refresh,
        )
    }
}
