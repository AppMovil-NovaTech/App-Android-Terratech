package com.novatech.terratech.inventory.domain.repository

import com.novatech.terratech.inventory.domain.entity.InventoryDraft
import com.novatech.terratech.inventory.domain.entity.InventoryItem
import com.novatech.terratech.inventory.domain.entity.InventorySnapshot

interface InventoryRepository {
    suspend fun load(): InventorySnapshot
    suspend fun register(draft: InventoryDraft): InventoryItem
    suspend fun updateStock(id: Int, quantity: Int): InventoryItem
}
