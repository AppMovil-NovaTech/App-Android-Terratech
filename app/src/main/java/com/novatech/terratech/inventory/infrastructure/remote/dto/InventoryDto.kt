package com.novatech.terratech.inventory.infrastructure.remote.dto

data class InventoryDto(
    val id: Int,
    val productId: Int,
    val fieldId: Int,
    val profileId: Int,
    val name: String,
    val quantity: Int,
    val unitType: String,
    val averageWeightPerUnitKg: Double,
    val totalWeightKg: Double,
    val price: Double,
    val createdAt: String?,
)
