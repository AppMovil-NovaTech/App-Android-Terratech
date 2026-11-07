package com.novatech.terratech.inventory.domain.entity

data class InventoryItem(
    val id: Int,
    val productId: Int,
    val fieldId: Int,
    val profileId: Int,
    val name: String,
    val stockQuantity: Int,
    val unitType: String,
    val averageWeightPerUnitKg: Double,
    val totalWeightKg: Double,
    val price: Double,
    val createdAt: String?,
)
