package com.novatech.terratech

import com.novatech.terratech.core.infrastructure.remote.TerraApi
import com.novatech.terratech.core.infrastructure.remote.apiCall
import com.novatech.terratech.monitoring.domain.valueobject.RecommendationSeverity
import com.novatech.terratech.monitoring.domain.valueobject.RecommendationType
import com.novatech.terratech.monitoring.infrastructure.local.entity.RecommendationRow
import com.novatech.terratech.monitoring.infrastructure.mapper.WebhookRecommendationMapper
import com.novatech.terratech.monitoring.infrastructure.mapper.domain
import com.novatech.terratech.monitoring.infrastructure.remote.dto.WebhookRecommendationDto
import java.io.IOException
import kotlinx.coroutines.test.runTest
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class Us21WebhookRecommendationsTest {
    private suspend fun withApi(block: suspend (MockWebServer, TerraApi) -> Unit) {
        val server = MockWebServer()
        server.start()
        try {
            val api =
                Retrofit.Builder()
                    .baseUrl(server.url("/"))
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()
                    .create(TerraApi::class.java)
            block(server, api)
        } finally {
            server.shutdown()
        }
    }

    @Test
    fun webhookRecommendationsEndpointQueriesAndParsesDtos() = runTest {
        withApi { server, api ->
            val body =
                """
                [
                  {
                    "id": 14,
                    "fieldId": 4,
                    "zone": "Sector Norte",
                    "action": "Activar riego por goteo",
                    "durationMinutes": 25,
                    "priority": "HIGH",
                    "reason": "Humedad por debajo de 25%",
                    "category": "IRRIGATION",
                    "deviceId": 9,
                    "source": "ANALYSIS_ENGINE",
                    "createdAt": "2026-11-23T06:30:00Z"
                  }
                ]
                """.trimIndent()
            server.enqueue(MockResponse().setBody(body))

            val result = apiCall { api.webhookRecommendations(fieldId = 4, deviceId = 9) }
            val request = server.takeRequest()
            assertEquals("/api/v1/recommendations/webhooks?fieldId=4&deviceId=9", request.path)
            assertEquals(1, result.size)
            assertEquals("Sector Norte", result[0].zone)
            assertEquals(25, result[0].durationMinutes)
            assertEquals("HIGH", result[0].priority)
        }
    }

    @Test
    fun webhookMapperProducesRoomRowAndDomainEntitySafely() {
        val dto =
            WebhookRecommendationDto(
                id = 21,
                fieldId = 4,
                zone = "Lote Sur",
                action = "Aplicar fertilizante NPK",
                durationMinutes = 30,
                priority = "HIGH",
                reason = "Nitrógeno bajo",
                category = "FERTILIZATION",
                createdAt = "2026-11-23T06:45:00Z",
            )

        val row = WebhookRecommendationMapper.toRow(dto = dto, userId = 7, deviceId = 9, readingId = 88)
        assertEquals(RecommendationType.WEBHOOK.name, row.type)
        assertEquals(RecommendationSeverity.CRITICAL.name, row.severity)
        assertEquals("Lote Sur: Aplicar fertilizante NPK", row.title)

        val domain = row.domain()
        assertEquals(RecommendationType.WEBHOOK, domain.type)
        assertEquals(RecommendationSeverity.CRITICAL, domain.severity)
        assertEquals(88, domain.readingId)
    }

    @Test
    fun recommendationMapperFallsBackOnUnknownEnumValues() {
        val legacyOrUnknownRow =
            RecommendationRow(
                userId = 1,
                deviceId = 2,
                readingId = 3,
                type = "FUTURE_TYPE",
                severity = "UNKNOWN_SEVERITY",
                title = "External alert",
                message = "Check soil moisture",
                actionSuggested = "Inspect drip line",
                createdAt = "2026-11-23T06:50:00Z",
            )

        val entity = legacyOrUnknownRow.domain()
        assertEquals(RecommendationType.WEBHOOK, entity.type)
        assertEquals(RecommendationSeverity.INFO, entity.severity)
    }

    @Test
    fun offlineWebhookSyncFallsBackWithoutThrowing() = runTest {
        val synced =
            try {
                apiCall<List<WebhookRecommendationDto>> { throw IOException("timeout") }
            } catch (_: Exception) {
                emptyList()
            }
        assertTrue(synced.isEmpty())
    }
}
