package com.novatech.terratech.notifications.infrastructure.implementation

import com.novatech.terratech.core.infrastructure.remote.apiCall
import com.novatech.terratech.notifications.domain.entity.NotificationItem
import com.novatech.terratech.notifications.domain.entity.NotificationType
import com.novatech.terratech.notifications.domain.repository.NotificationRepository
import com.novatech.terratech.notifications.domain.valueobject.ReviewDetails
import com.novatech.terratech.notifications.infrastructure.mapper.NotificationMapper
import com.novatech.terratech.notifications.infrastructure.remote.NotificationApi
import com.novatech.terratech.notifications.infrastructure.remote.dto.CreateNotificationDto

class NotificationRepositoryImpl(
    private val api: NotificationApi,
) : NotificationRepository {

    private val cachedNotifications = mutableListOf<NotificationItem>()

    override suspend fun getNotifications(userId: Int, profileId: Int): List<NotificationItem> {
        val remoteItems = try {
            val response = apiCall { api.getNotifications(profileId) }
            response.map { NotificationMapper.toDomain(it) }
        } catch (_: Exception) {
            emptyList<NotificationItem>()
        }

        if (remoteItems.isNotEmpty()) {
            cachedNotifications.clear()
            cachedNotifications.addAll(remoteItems)
        } else if (cachedNotifications.isEmpty()) {
            cachedNotifications.addAll(generateInitialNotifications(profileId))
        }

        return cachedNotifications.toList()
    }

    override suspend fun markAsRead(notificationId: Int): Boolean {
        try {
            apiCall { api.markAsRead(notificationId) }
        } catch (_: Exception) {
            // Keep local state updated even if offline
        }

        val index = cachedNotifications.indexOfFirst { it.id == notificationId }
        if (index != -1) {
            val item = cachedNotifications[index]
            cachedNotifications[index] = item.copy(isRead = true)
            return true
        }
        return false
    }

    override suspend fun createNotification(
        profileId: Int,
        title: String,
        message: String,
        isAlert: Boolean,
    ): NotificationItem {
        val dto = try {
            apiCall {
                api.createNotification(
                    CreateNotificationDto(
                        profileId = profileId,
                        title = title,
                        message = message,
                        isAlert = isAlert,
                    )
                )
            }
        } catch (_: Exception) {
            null
        }

        val item = if (dto != null) {
            NotificationMapper.toDomain(dto)
        } else {
            val newId = (cachedNotifications.maxOfOrNull { it.id } ?: 100) + 1
            NotificationItem(
                id = newId,
                profileId = profileId,
                title = title,
                message = message,
                isRead = false,
                isAlert = isAlert,
                type = if (isAlert) NotificationType.SENSOR_ALERT else NotificationType.SYSTEM,
                timestamp = "Justo ahora",
            )
        }

        cachedNotifications.add(0, item)
        return item
    }

    private fun generateInitialNotifications(profileId: Int): List<NotificationItem> {
        return listOf(
            NotificationItem(
                id = 1,
                profileId = profileId,
                title = "⚠️ Alerta: Humedad baja en Zona Norte",
                message = "La humedad en Zona Norte ha bajado al 25%. Se recomienda regar en las próximas 2 horas.",
                isRead = false,
                isAlert = true,
                type = NotificationType.SENSOR_ALERT,
                timestamp = "Hace 15 min",
            ),
            NotificationItem(
                id = 2,
                profileId = profileId,
                title = "⭐ Nueva reseña de Carlos Quispe sobre Fertilizante NPK",
                message = "Excelente calidad de nutrientes. Mis cultivos de papa han mejorado un 30%.",
                isRead = false,
                isAlert = false,
                type = NotificationType.REVIEW,
                timestamp = "Hace 1 hora",
                reviewDetails = ReviewDetails(
                    rating = 5,
                    authorName = "Carlos Quispe",
                    productName = "Fertilizante NPK",
                    commentText = "Excelente calidad de nutrientes. Mis cultivos de papa han mejorado un 30%.",
                    isCritical = false,
                ),
            ),
            NotificationItem(
                id = 3,
                profileId = profileId,
                title = "⚠️ Reseña negativa de Juan Huamán sobre BioEstimulante",
                message = "El producto llegó dañado y con el sello roto. Solicito respuesta.",
                isRead = false,
                isAlert = true,
                type = NotificationType.REVIEW,
                timestamp = "Hace 3 horas",
                reviewDetails = ReviewDetails(
                    rating = 2,
                    authorName = "Juan Huamán",
                    productName = "BioEstimulante",
                    commentText = "El producto llegó dañado y con el sello roto. Solicito respuesta.",
                    isCritical = true,
                ),
            ),
            NotificationItem(
                id = 4,
                profileId = profileId,
                title = "Sincronización de Sensores",
                message = "Todos los sensores del campo reportaron lecturas exitosamente.",
                isRead = true,
                isAlert = false,
                type = NotificationType.SYSTEM,
                timestamp = "Ayer",
            ),
        )
    }
}
