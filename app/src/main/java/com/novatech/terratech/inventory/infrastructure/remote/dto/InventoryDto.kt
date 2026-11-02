package com.novatech.terratech.inventory.infrastructure.remote.dto

data class InventoryDto(
    val id: Int,
    val productId: Int,
    val stockQuantity: Int,
    val warehouseLocation: String?,
    val createdAt: String?,
)
