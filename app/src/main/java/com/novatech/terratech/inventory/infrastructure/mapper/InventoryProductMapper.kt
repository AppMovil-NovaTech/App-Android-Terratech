package com.novatech.terratech.inventory.infrastructure.mapper

import com.novatech.terratech.inventory.domain.entity.InventoryProduct
import com.novatech.terratech.inventory.infrastructure.remote.dto.InventoryProductDto

fun InventoryProductDto.domain(): InventoryProduct =
    InventoryProduct(id, name, description.orEmpty(), price, type, imageUrl)
