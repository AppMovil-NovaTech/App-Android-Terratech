package com.novatech.terratech

import com.novatech.terratech.core.domain.Failure
import com.novatech.terratech.core.infrastructure.remote.TerraApi
import com.novatech.terratech.core.infrastructure.remote.apiCall
import com.novatech.terratech.monitoring.domain.entity.Sensor
import com.novatech.terratech.monitoring.presentation.format.ElapsedTimeFormatter
import java.time.Instant
import kotlinx.coroutines.test.runTest
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Assert.fail
import org.junit.Test
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class Us20SensorReadingsTest {
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
    fun latestDtoDeserializesUs20MetadataFields() = runTest {
        withApi { server, api ->
            val payload =
                """
                {
                  "reading": {
                    "id": 99,
                    "deviceId": 5,
                    "recordedAt": "2026-11-23T06:00:00Z",
                    "moisturePercent": 38.4,
                    "soilTemperatureC": 21.5,
                    "nitrogenPpm": 28.0,
                    "phosphorusPpm": 16.0,
                    "potassiumPpm": 42.0,
                    "source": "SIMULATED"
                  },
                  "isStale": true,
                  "fieldId": 12,
                  "sensorCode": "TT-US2001",
                  "batteryLevelPercent": 15,
                  "elapsedMinutes": 75
                }
                """.trimIndent()
            server.enqueue(MockResponse().setBody(payload))

            val latest = apiCall { api.latest(5) }
            assertEquals("/api/v1/devices/5/readings/latest", server.takeRequest().path)
            assertTrue(latest.isStale)
            assertEquals(12, latest.fieldId)
            assertEquals("TT-US2001", latest.sensorCode)
            assertEquals(15, latest.batteryLevelPercent)
            assertEquals(75, latest.elapsedMinutes)
        }
    }

    @Test
    fun elapsedTimeFormatterFormatsMinutesAndHours() {
        val recorded = Instant.parse("2026-11-23T06:00:00Z")
        val now = recorded.plusSeconds(45 * 60L)

        assertEquals(45, ElapsedTimeFormatter.elapsedMinutes(recorded, now))
        assertEquals("45 min", ElapsedTimeFormatter.format(45))
        assertEquals("1 h", ElapsedTimeFormatter.format(60))
        assertEquals("2 h 15 min", ElapsedTimeFormatter.format(135))
        assertEquals("0 min", ElapsedTimeFormatter.format(-5))
    }

    @Test
    fun sensorBatteryFallsBackFromDeviceStatus() {
        val explicit = Sensor(1, 2, "Norte", "TT-AAA111", "02:00:00:00:00:01", "ONLINE", 72)
        val online = Sensor(2, 2, "Sur", "TT-AAA222", "02:00:00:00:00:02", "ONLINE")
        val lowBattery = Sensor(3, 2, "Este", "TT-AAA333", "02:00:00:00:00:03", "LOW_BATTERY")
        val offline = Sensor(4, 2, "Oeste", "TT-AAA444", "02:00:00:00:00:04", "OFFLINE")

        assertEquals(72, explicit.resolvedBatteryPercent())
        assertEquals(90, online.resolvedBatteryPercent())
        assertEquals(15, lowBattery.resolvedBatteryPercent())
        assertNull(offline.resolvedBatteryPercent())
    }

    @Test
    fun apiCallDistinguishesDeviceNotFoundFromNoReadings() = runTest {
        withApi { server, api ->
            server.enqueue(MockResponse().setResponseCode(404).setBody("""{"code":"DEVICE_NOT_FOUND"}"""))
            try {
                apiCall { api.latest(999999) }
                fail("Expected DEVICE_NOT_FOUND Failure")
            } catch (failure: Failure) {
                assertEquals(404, failure.status)
                assertEquals("DEVICE_NOT_FOUND", failure.code)
            }

            server.enqueue(MockResponse().setResponseCode(404).setBody("""{"code":"NO_READINGS"}"""))
            try {
                apiCall { api.latest(7) }
                fail("Expected NO_READINGS Failure")
            } catch (failure: Failure) {
                assertEquals(404, failure.status)
                assertEquals("NO_READINGS", failure.code)
            }
        }
    }
}
