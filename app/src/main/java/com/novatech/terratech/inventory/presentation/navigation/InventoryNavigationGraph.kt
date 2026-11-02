package com.novatech.terratech.inventory.presentation.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.navigation
import com.novatech.terratech.core.presentation.navigation.AppNavigator
import com.novatech.terratech.inventory.presentation.state.InventoryState

fun NavGraphBuilder.inventoryGraph(
    navigator: AppNavigator,
    state: InventoryState,
    actions: InventoryNavigationActions,
) {
    navigation<InventoryGraph>(startDestination = InventoryDestination) {
        inventoryDestination(navigator, state, actions)
        createInventoryDestination(navigator, state, actions)
        editInventoryDestination(navigator, state, actions)
    }
}
