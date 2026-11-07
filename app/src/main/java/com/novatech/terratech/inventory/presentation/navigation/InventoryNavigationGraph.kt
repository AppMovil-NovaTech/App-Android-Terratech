package com.novatech.terratech.inventory.presentation.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.navigation
import com.novatech.terratech.core.presentation.navigation.AppNavigator
import com.novatech.terratech.inventory.presentation.state.InventoryState
import com.novatech.terratech.monitoring.domain.entity.Field

fun NavGraphBuilder.inventoryGraph(
    navigator: AppNavigator,
    state: InventoryState,
    actions: InventoryNavigationActions,
    profileId: Int?,
    fields: List<Field>,
) {
    navigation<InventoryGraph>(startDestination = InventoryDestination) {
        inventoryDestination(navigator, state, actions, fields)
        createInventoryDestination(navigator, state, actions, profileId, fields)
        editInventoryDestination(navigator, state, actions)
    }
}
