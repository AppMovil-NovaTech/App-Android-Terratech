package com.novatech.terratech.inventory.presentation.state

import com.novatech.terratech.inventory.domain.entity.InventoryItem
import com.novatech.terratech.inventory.domain.entity.InventoryProduct

data class InventoryState(
    val userId: Int? = null,
    val items: List<InventoryItem> = emptyList(),
    val products: List<InventoryProduct> = emptyList(),
    val busy: Boolean = false,
    val error: String? = null,
    val saved: Boolean = false,
    val partialProductCreated: Boolean = false,
)
