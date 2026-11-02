package com.novatech.terratech.inventory.infrastructure.mapper

import com.novatech.terratech.inventory.domain.entity.InventoryItem
import com.novatech.terratech.inventory.infrastructure.remote.dto.InventoryDto

fun InventoryDto.domain(): InventoryItem =
    InventoryItem(id, productId, stockQuantity, warehouseLocation.orEmpty(), createdAt)
