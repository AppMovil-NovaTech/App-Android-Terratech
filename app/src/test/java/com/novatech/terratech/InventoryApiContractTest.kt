package com.novatech.terratech

import com.google.gson.JsonParser
import com.novatech.terratech.inventory.infrastructure.implementation.InventoryRepositoryImpl
import com.novatech.terratech.inventory.infrastructure.remote.InventoryApi
import kotlinx.coroutines.test.runTest
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.Assert.assertEquals
import org.junit.Test
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class InventoryApiContractTest {
    @Test
    fun inventoryConsultCreateAndUpdateUseExistingBackendRoutes() = runTest {
        val server = MockWebServer()
        server.start()
        try {
            val api =
                Retrofit.Builder()
                    .baseUrl(server.url("/"))
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()
                    .create(InventoryApi::class.java)
            val repository = InventoryRepositoryImpl(api)

            server.enqueue(
                MockResponse().setBody(
                    """[{"id":3,"productId":9,"stockQuantity":12,"warehouseLocation":"Almacén A","createdAt":"2026-11-01T00:00:00Z"}]"""
                )
            )
            server.enqueue(
                MockResponse().setBody(
                    """[{"id":9,"name":"Fertilizante NPK","description":"Bolsa","price":25.5,"type":"Fertilizante","imageUrl":""}]"""
                )
            )
            val snapshot = repository.load()
            assertEquals(12, snapshot.items.single().stockQuantity)
            assertEquals("Fertilizante NPK", snapshot.products.single().name)
            assertEquals("/api/v1/inventories", server.takeRequest().path)
            assertEquals("/api/v1/products", server.takeRequest().path)

            server.enqueue(
                MockResponse().setResponseCode(201).setBody(
                    """{"id":4,"productId":9,"stockQuantity":8,"warehouseLocation":"B"}"""
                )
            )
            val created = repository.register(9, 8, "B")
            assertEquals(4, created.id)
            val createRequest = server.takeRequest()
            assertEquals("POST", createRequest.method)
            assertEquals("/api/v1/inventories", createRequest.path)
            val createJson = JsonParser.parseString(createRequest.body.readUtf8()).asJsonObject
            assertEquals(9, createJson["productId"].asInt)
            assertEquals(8, createJson["stockQuantity"].asInt)

            server.enqueue(
                MockResponse().setBody(
                    """{"id":4,"productId":9,"stockQuantity":0,"warehouseLocation":"B"}"""
                )
            )
            assertEquals(0, repository.updateStock(4, 0).stockQuantity)
            val updateRequest = server.takeRequest()
            assertEquals("PUT", updateRequest.method)
            assertEquals("/api/v1/inventories/4", updateRequest.path)
            assertEquals(
                0,
                JsonParser.parseString(updateRequest.body.readUtf8())
                    .asJsonObject["stockQuantity"].asInt,
            )
        } finally {
            server.shutdown()
        }
    }

    @Test
    fun aNewSupplyUsesProductsEndpointBeforeInventory() = runTest {
        val server = MockWebServer()
        server.start()
        try {
            val api =
                Retrofit.Builder()
                    .baseUrl(server.url("/"))
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()
                    .create(InventoryApi::class.java)
            val repository = InventoryRepositoryImpl(api)
            server.enqueue(
                MockResponse().setResponseCode(201).setBody(
                    """{"id":15,"name":"Abono orgánico","description":"Saco","price":9.5,"type":"Abono","imageUrl":""}"""
                )
            )
            val product = repository.addProduct("Abono orgánico", "Saco", 9.5, "Abono")
            assertEquals(15, product.id)
            val request = server.takeRequest()
            assertEquals("/api/v1/products", request.path)
            val json = JsonParser.parseString(request.body.readUtf8()).asJsonObject
            assertEquals("Abono", json["type"].asString)
            assertEquals(9.5, json["price"].asDouble, 0.0)
        } finally {
            server.shutdown()
        }
    }
}
