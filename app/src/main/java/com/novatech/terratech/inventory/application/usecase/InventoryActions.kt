package com.novatech.terratech.inventory.application.usecase

import com.novatech.terratech.inventory.domain.entity.InventoryDraft
import com.novatech.terratech.inventory.domain.repository.InventoryRepository

class InventoryActions(private val repository: InventoryRepository) {
    suspend fun load() = repository.load()
    suspend fun register(draft: InventoryDraft) = repository.register(draft)
    suspend fun updateStock(id: Int, quantity: Int) = repository.updateStock(id, quantity)
}
