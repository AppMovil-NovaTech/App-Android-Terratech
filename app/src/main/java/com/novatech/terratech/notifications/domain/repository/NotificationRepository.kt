package com.novatech.terratech.notifications.domain.repository

import com.novatech.terratech.notifications.domain.entity.NotificationItem

interface NotificationRepository {
    suspend fun getNotifications(userId: Int, profileId: Int): List<NotificationItem>
    suspend fun markAsRead(notificationId: Int): Boolean
    suspend fun createNotification(profileId: Int, title: String, message: String, isAlert: Boolean): NotificationItem
}
