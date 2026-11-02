package com.novatech.terratech.inventory.presentation.navigation

import androidx.compose.runtime.LaunchedEffect
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.novatech.terratech.core.presentation.navigation.AppNavigator
import com.novatech.terratech.inventory.presentation.state.InventoryState
import com.novatech.terratech.inventory.presentation.ui.EditInventoryScreen

fun NavGraphBuilder.editInventoryDestination(
    navigator: AppNavigator,
    state: InventoryState,
    actions: InventoryNavigationActions,
) {
    composable<EditInventoryDestination> { entry ->
        val destination = entry.toRoute<EditInventoryDestination>()
        LaunchedEffect(Unit) { actions.clearMessage() }
        LaunchedEffect(state.saved) {
            if (state.saved) {
                actions.clearMessage()
                navigator.back()
            }
        }
        EditInventoryScreen(
            item = state.items.find { it.id == destination.inventoryId },
            product = state.products.find {
                it.id == state.items.find { row -> row.id == destination.inventoryId }?.productId
            },
            busy = state.busy,
            error = state.error,
            onSave = { quantity -> actions.updateStock(destination.inventoryId, quantity) },
            onCancel = navigator::back,
        )
    }
}
