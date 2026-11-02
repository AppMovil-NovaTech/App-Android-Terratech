package com.novatech.terratech.inventory.infrastructure.implementation

import com.novatech.terratech.core.infrastructure.remote.apiCall
import com.novatech.terratech.inventory.domain.entity.InventoryItem
import com.novatech.terratech.inventory.domain.entity.InventoryProduct
import com.novatech.terratech.inventory.domain.entity.InventorySnapshot
import com.novatech.terratech.inventory.domain.repository.InventoryRepository
import com.novatech.terratech.inventory.infrastructure.mapper.domain
import com.novatech.terratech.inventory.infrastructure.remote.InventoryApi
import com.novatech.terratech.inventory.infrastructure.remote.dto.CreateInventoryDto
import com.novatech.terratech.inventory.infrastructure.remote.dto.CreateInventoryProductDto
import com.novatech.terratech.inventory.infrastructure.remote.dto.UpdateStockDto

class InventoryRepositoryImpl(private val api: InventoryApi) : InventoryRepository {
    override suspend fun load(): InventorySnapshot {
        val items = apiCall { api.inventories() }.map { it.domain() }
        val products = apiCall { api.products() }.map { it.domain() }
        return InventorySnapshot(items, products)
    }

    override suspend fun register(productId: Int, quantity: Int, location: String): InventoryItem {
        require(productId > 0 && quantity >= 0)
        return apiCall {
            api.registerInventory(CreateInventoryDto(productId, quantity, location.trim()))
        }.domain()
    }

    override suspend fun updateStock(id: Int, quantity: Int): InventoryItem {
        require(id > 0 && quantity >= 0)
        return apiCall { api.updateInventory(id, UpdateStockDto(quantity)) }.domain()
    }

    override suspend fun addProduct(
        name: String,
        description: String,
        price: Double,
        type: String,
    ): InventoryProduct {
        require(name.isNotBlank() && type.isNotBlank() && price.isFinite() && price >= 0.0)
        return apiCall {
            api.registerProduct(
                CreateInventoryProductDto(name.trim(), description.trim(), price, type.trim())
            )
        }.domain()
    }
}
