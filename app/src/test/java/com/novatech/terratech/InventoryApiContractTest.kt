package com.novatech.terratech

import com.google.gson.JsonParser
import com.novatech.terratech.inventory.domain.entity.InventoryDraft
import com.novatech.terratech.inventory.infrastructure.implementation.InventoryRepositoryImpl
import com.novatech.terratech.inventory.infrastructure.remote.InventoryApi
import kotlinx.coroutines.test.runTest
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class InventoryApiContractTest {
    private val item =
        """{"id":4,"productId":9,"fieldId":12,"profileId":1,"name":"Papa amarilla","quantity":8,"unitType":"Sacks","averageWeightPerUnitKg":25.0,"totalWeightKg":200.0,"price":2.5,"createdAt":"2026-11-07T00:00:00Z"}"""

    private fun api(server: MockWebServer): InventoryApi =
        Retrofit.Builder()
            .baseUrl(server.url("/"))
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(InventoryApi::class.java)

    @Test
    fun inventoryListUsesApprovedResponseWithoutProductsRequest() = runTest {
        val server = MockWebServer()
        server.start()
        try {
            val repository = InventoryRepositoryImpl(api(server))
            server.enqueue(MockResponse().setBody("[$item]"))
            val snapshot = repository.load()
            assertEquals(1, snapshot.items.size)
            assertEquals("Papa amarilla", snapshot.items.single().name)
            assertEquals(8, snapshot.items.single().stockQuantity)
            assertEquals(12, snapshot.items.single().fieldId)
            assertEquals("/api/v1/inventories", server.takeRequest().path)
            assertEquals(1, server.requestCount)
        } finally { server.shutdown() }
    }

    @Test
    fun registerHarvestSendsEightFieldsInOneRequest() = runTest {
        val server = MockWebServer()
        server.start()
        try {
            val repository = InventoryRepositoryImpl(api(server))
            server.enqueue(MockResponse().setResponseCode(201).setBody(item))
            val draft = InventoryDraft("Papa amarilla", 12, 1, 8, "Sacks", 25.0, 2.5, "https://example.com/papa.jpg")
            val created = repository.register(draft)
            assertEquals(4, created.id)
            assertEquals(9, created.productId)
            val request = server.takeRequest()
            assertEquals("POST", request.method)
            assertEquals("/api/v1/inventories", request.path)
            val json = JsonParser.parseString(request.body.readUtf8()).asJsonObject
            assertEquals(8, json.size())
            assertEquals("Papa amarilla", json["name"].asString)
            assertEquals(12, json["fieldId"].asInt)
            assertEquals(1, json["profileId"].asInt)
            assertEquals(8, json["quantity"].asInt)
            assertEquals("Sacks", json["unitType"].asString)
            assertEquals(25.0, json["averageWeightPerUnitKg"].asDouble, 0.001)
            assertEquals(2.5, json["price"].asDouble, 0.001)
            assertEquals("https://example.com/papa.jpg", json["imageUrl"].asString)
            assertFalse(json.has("warehouseLocation"))
            assertFalse(json.has("stockQuantity"))
            assertEquals(1, server.requestCount)
        } finally { server.shutdown() }
    }

    @Test
    fun updatingStockSendsStockQuantityAndParsesNewResponse() = runTest {
        val server = MockWebServer()
        server.start()
        try {
            val repository = InventoryRepositoryImpl(api(server))
            server.enqueue(MockResponse().setBody(item.replace("\"quantity\":8", "\"quantity\":0").replace("\"totalWeightKg\":200.0", "\"totalWeightKg\":0.0")))
            val updated = repository.updateStock(4, 0)
            assertEquals(0, updated.stockQuantity)
            assertEquals(0.0, updated.totalWeightKg, 0.001)
            val request = server.takeRequest()
            assertEquals("PUT", request.method)
            assertEquals("/api/v1/inventories/4", request.path)
            val json = JsonParser.parseString(request.body.readUtf8()).asJsonObject
            assertEquals(0, json["stockQuantity"].asInt)
            assertTrue(json.size() == 1)
        } finally { server.shutdown() }
    }
}
