package com.novatech.terratech.inventory.domain.entity

data class InventorySnapshot(
    val items: List<InventoryItem>,
    val products: List<InventoryProduct>,
)
