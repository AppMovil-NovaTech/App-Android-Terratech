package com.novatech.terratech.inventory.infrastructure.implementation

import com.novatech.terratech.core.infrastructure.remote.apiCall
import com.novatech.terratech.inventory.domain.entity.InventoryDraft
import com.novatech.terratech.inventory.domain.entity.InventoryItem
import com.novatech.terratech.inventory.domain.entity.InventorySnapshot
import com.novatech.terratech.inventory.domain.repository.InventoryRepository
import com.novatech.terratech.inventory.infrastructure.mapper.domain
import com.novatech.terratech.inventory.infrastructure.remote.InventoryApi
import com.novatech.terratech.inventory.infrastructure.remote.dto.CreateInventoryDto
import com.novatech.terratech.inventory.infrastructure.remote.dto.UpdateStockDto

class InventoryRepositoryImpl(private val api: InventoryApi) : InventoryRepository {
    override suspend fun load(): InventorySnapshot =
        InventorySnapshot(apiCall { api.inventories() }.map { it.domain() })

    override suspend fun register(draft: InventoryDraft): InventoryItem {
        require(draft.name.isNotBlank() && draft.fieldId > 0 && draft.profileId > 0)
        require(draft.quantity > 0 && draft.unitType.isNotBlank())
        require(draft.averageWeightPerUnitKg.isFinite() && draft.averageWeightPerUnitKg > 0)
        require(draft.price.isFinite() && draft.price > 0)
        require(draft.imageUrl.isNotBlank())
        return apiCall {
            api.registerInventory(
                CreateInventoryDto(
                    name = draft.name.trim(),
                    fieldId = draft.fieldId,
                    profileId = draft.profileId,
                    quantity = draft.quantity,
                    unitType = draft.unitType.trim(),
                    averageWeightPerUnitKg = draft.averageWeightPerUnitKg,
                    price = draft.price,
                    imageUrl = draft.imageUrl.trim(),
                )
            )
        }.domain()
    }

    override suspend fun updateStock(id: Int, quantity: Int): InventoryItem {
        require(id > 0 && quantity >= 0)
        return apiCall { api.updateInventory(id, UpdateStockDto(quantity)) }.domain()
    }
}
