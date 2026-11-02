package com.novatech.terratech.inventory.infrastructure.remote.dto

data class CreateInventoryDto(
    val productId: Int,
    val stockQuantity: Int,
    val warehouseLocation: String,
)
