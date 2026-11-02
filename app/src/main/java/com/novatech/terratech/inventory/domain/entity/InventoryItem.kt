package com.novatech.terratech.inventory.domain.entity

data class InventoryItem(
    val id: Int,
    val productId: Int,
    val stockQuantity: Int,
    val warehouseLocation: String,
    val createdAt: String?,
)
