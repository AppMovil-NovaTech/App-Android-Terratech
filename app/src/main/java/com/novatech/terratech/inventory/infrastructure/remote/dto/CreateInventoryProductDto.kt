package com.novatech.terratech.inventory.infrastructure.remote.dto

data class CreateInventoryProductDto(
    val name: String,
    val description: String,
    val price: Double,
    val type: String,
    val imageUrl: String = "",
)
