package com.novatech.terratech.inventory.domain.entity

data class InventoryDraft(
    val name: String,
    val fieldId: Int,
    val profileId: Int,
    val quantity: Int,
    val unitType: String,
    val averageWeightPerUnitKg: Double,
    val price: Double,
    val imageUrl: String,
)
