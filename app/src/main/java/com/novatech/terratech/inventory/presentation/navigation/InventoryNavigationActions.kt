package com.novatech.terratech.inventory.presentation.navigation

class InventoryNavigationActions(
    val refresh: () -> Unit,
    val clearMessage: () -> Unit,
    val registerExisting: (Int, Int, String) -> Unit,
    val registerNew: (String, String, Double, String, Int, String) -> Unit,
    val updateStock: (Int, Int) -> Unit,
)
