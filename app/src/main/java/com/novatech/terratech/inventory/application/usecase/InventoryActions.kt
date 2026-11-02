package com.novatech.terratech.inventory.application.usecase

import com.novatech.terratech.inventory.domain.repository.InventoryRepository

class InventoryActions(private val repository: InventoryRepository) {
    suspend fun load() = repository.load()

    suspend fun register(productId: Int, quantity: Int, location: String) =
        repository.register(productId, quantity, location)

    suspend fun updateStock(id: Int, quantity: Int) = repository.updateStock(id, quantity)

    suspend fun addProduct(name: String, description: String, price: Double, type: String) =
        repository.addProduct(name, description, price, type)
}
