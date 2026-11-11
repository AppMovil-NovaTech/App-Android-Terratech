package com.novatech.terratech.inventory.presentation.navigation

import com.novatech.terratech.inventory.domain.entity.InventoryDraft

class InventoryNavigationActions(
    val refresh: () -> Unit,
    val clearMessage: () -> Unit,
    val register: (InventoryDraft) -> Unit,
    val updateStock: (Int, Int) -> Unit,
)
