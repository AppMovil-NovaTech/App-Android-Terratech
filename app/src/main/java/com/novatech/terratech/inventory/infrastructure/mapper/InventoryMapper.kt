package com.novatech.terratech.inventory.infrastructure.mapper

import com.novatech.terratech.inventory.domain.entity.InventoryItem
import com.novatech.terratech.inventory.infrastructure.remote.dto.InventoryDto

fun InventoryDto.domain(): InventoryItem =
    InventoryItem(
        id = id,
        productId = productId,
        fieldId = fieldId,
        profileId = profileId,
        name = name,
        stockQuantity = quantity,
        unitType = unitType,
        averageWeightPerUnitKg = averageWeightPerUnitKg,
        totalWeightKg = totalWeightKg,
        price = price,
        createdAt = createdAt,
    )
