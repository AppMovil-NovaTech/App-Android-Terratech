package com.novatech.terratech.inventory.infrastructure.remote

import com.novatech.terratech.inventory.infrastructure.remote.dto.CreateInventoryDto
import com.novatech.terratech.inventory.infrastructure.remote.dto.InventoryDto
import com.novatech.terratech.inventory.infrastructure.remote.dto.UpdateStockDto
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface InventoryApi {
    @GET("api/v1/inventories")
    suspend fun inventories(): List<InventoryDto>

    @POST("api/v1/inventories")
    suspend fun registerInventory(@Body body: CreateInventoryDto): InventoryDto

    @PUT("api/v1/inventories/{id}")
    suspend fun updateInventory(@Path("id") id: Int, @Body body: UpdateStockDto): InventoryDto
}
