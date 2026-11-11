package com.novatech.terratech.inventory.presentation.navigation

import androidx.compose.runtime.LaunchedEffect
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.novatech.terratech.core.presentation.navigation.AppNavigator
import com.novatech.terratech.inventory.presentation.state.InventoryState
import com.novatech.terratech.inventory.presentation.ui.InventoryScreen
import com.novatech.terratech.monitoring.domain.entity.Field

fun NavGraphBuilder.inventoryDestination(
    navigator: AppNavigator,
    state: InventoryState,
    actions: InventoryNavigationActions,
    fields: List<Field>,
) {
    composable<InventoryDestination> {
        LaunchedEffect(Unit) { actions.refresh() }
        InventoryScreen(
            state = state,
            fields = fields,
            onCreate = navigator::createInventory,
            onEdit = navigator::editInventory,
            onRefresh = actions.refresh,
        )
    }
}
