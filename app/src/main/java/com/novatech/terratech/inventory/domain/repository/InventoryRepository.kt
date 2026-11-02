package com.novatech.terratech.inventory.domain.repository

import com.novatech.terratech.inventory.domain.entity.InventoryItem
import com.novatech.terratech.inventory.domain.entity.InventoryProduct
import com.novatech.terratech.inventory.domain.entity.InventorySnapshot

interface InventoryRepository {
    suspend fun load(): InventorySnapshot
    suspend fun register(productId: Int, quantity: Int, location: String): InventoryItem
    suspend fun updateStock(id: Int, quantity: Int): InventoryItem
    suspend fun addProduct(
        name: String,
        description: String,
        price: Double,
        type: String,
    ): InventoryProduct
}
