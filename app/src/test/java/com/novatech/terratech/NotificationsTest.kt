package com.novatech.terratech

import com.novatech.terratech.notifications.application.usecase.NotificationActions
import com.novatech.terratech.notifications.domain.entity.NotificationItem
import com.novatech.terratech.notifications.domain.entity.NotificationPreferences
import com.novatech.terratech.notifications.domain.entity.NotificationType
import com.novatech.terratech.notifications.domain.repository.NotificationPreferencesRepository
import com.novatech.terratech.notifications.domain.repository.NotificationRepository
import com.novatech.terratech.notifications.infrastructure.mapper.NotificationMapper
import com.novatech.terratech.notifications.infrastructure.remote.dto.NotificationDto
import com.novatech.terratech.notifications.presentation.state.NotificationFilter
import com.novatech.terratech.notifications.presentation.state.NotificationsState
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class NotificationsTest {

    @Test
    fun mapperIdentifiesSensorAlertAndPreservesCriticalState() {
        val dto = NotificationDto(
            id = 101,
            profileId = 1,
            title = "⚠️ Alerta: Humedad baja en Zona Norte",
            message = "La humedad en Zona Norte ha bajado al 25%. Se recomienda regar en las próximas 2 horas.",
            isRead = false,
            isAlert = true,
            createdAt = "2026-11-09T18:00:00Z",
        )

        val domain = NotificationMapper.toDomain(dto)

        assertEquals(NotificationType.SENSOR_ALERT, domain.type)
        assertTrue(domain.isAlert)
        assertFalse(domain.isRead)
        assertEquals(101, domain.id)
    }

    @Test
    fun mapperIdentifiesPositiveReviewWithAction() {
        val dto = NotificationDto(
            id = 102,
            profileId = 1,
            title = "⭐ Nueva reseña de Carlos sobre Fertilizante NPK - 5 estrellas",
            message = "Excelente calidad de producto para mis cultivos.",
            isRead = false,
            isAlert = false,
        )

        val domain = NotificationMapper.toDomain(dto)

        assertEquals(NotificationType.REVIEW, domain.type)
        assertNotNull(domain.reviewDetails)
        assertEquals(5, domain.reviewDetails?.rating)
        assertFalse(domain.reviewDetails?.isCritical == true)
        assertEquals("Carlos", domain.reviewDetails?.authorName)
    }

    @Test
    fun mapperIdentifiesNegativeReviewAsHighPriority() {
        val dto = NotificationDto(
            id = 103,
            profileId = 1,
            title = "⚠️ Reseña negativa de Juan sobre Abono Orgánico - 2 estrellas",
            message = "El empaque llegó roto y con humedad.",
            isRead = false,
            isAlert = true,
        )

        val domain = NotificationMapper.toDomain(dto)

        assertEquals(NotificationType.REVIEW, domain.type)
        assertNotNull(domain.reviewDetails)
        assertEquals(2, domain.reviewDetails?.rating)
        assertTrue(domain.reviewDetails?.isCritical == true)
        assertTrue(domain.isAlert)
    }

    @Test
    fun actionsFilterOutHumidityAlertsWhenPreferenceDisabled() = runBlocking {
        val mockItems = listOf(
            NotificationItem(
                id = 1,
                profileId = 1,
                title = "Alerta: Humedad baja en Zona Norte",
                message = "Humedad al 20%",
                isRead = false,
                isAlert = true,
                type = NotificationType.SENSOR_ALERT,
                timestamp = "Ahora",
            ),
            NotificationItem(
                id = 2,
                profileId = 1,
                title = "Alerta: Nitrógeno crítico",
                message = "Nivel bajo de N en suelo",
                isRead = false,
                isAlert = true,
                type = NotificationType.SENSOR_ALERT,
                timestamp = "Ahora",
            ),
        )

        val fakeNotifRepo = object : NotificationRepository {
            override suspend fun getNotifications(userId: Int, profileId: Int) = mockItems
            override suspend fun markAsRead(notificationId: Int) = true
            override suspend fun createNotification(profileId: Int, title: String, message: String, isAlert: Boolean) = mockItems.first()
        }

        val fakePrefRepo = object : NotificationPreferencesRepository {
            private var current = NotificationPreferences(
                userId = 1,
                enableHumidityAlerts = false,
                enableNutrientAlerts = true,
            )
            override suspend fun getPreferences(userId: Int) = current
            override suspend fun savePreferences(preferences: NotificationPreferences): NotificationPreferences {
                current = preferences
                return current
            }
        }

        val actions = NotificationActions(fakeNotifRepo, fakePrefRepo)
        val filtered = actions.getNotifications(1, 1)

        assertEquals(1, filtered.size)
        assertEquals("Alerta: Nitrógeno crítico", filtered.first().title)
    }

    @Test
    fun stateOrdersUnreadItemsFirstAndCountsUnreadAccurately() {
        val items = listOf(
            NotificationItem(id = 1, profileId = 1, title = "Item 1", message = "Msg", isRead = true, isAlert = false, type = NotificationType.SYSTEM, timestamp = "1"),
            NotificationItem(id = 2, profileId = 1, title = "Item 2", message = "Msg", isRead = false, isAlert = true, type = NotificationType.SENSOR_ALERT, timestamp = "2"),
            NotificationItem(id = 3, profileId = 1, title = "Item 3", message = "Msg", isRead = false, isAlert = false, type = NotificationType.REVIEW, timestamp = "3"),
        )

        val state = NotificationsState(items = items, filter = NotificationFilter.ALL)

        assertEquals(2, state.unreadCount)
        val ordered = state.filteredItems
        assertFalse(ordered[0].isRead)
        assertFalse(ordered[1].isRead)
        assertTrue(ordered[2].isRead)
    }
}
