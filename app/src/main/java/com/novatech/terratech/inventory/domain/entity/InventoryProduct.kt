package com.novatech.terratech.inventory.domain.entity

data class InventoryProduct(
    val id: Int,
    val name: String,
    val description: String,
    val price: Double,
    val type: String,
    val imageUrl: String?,
)
