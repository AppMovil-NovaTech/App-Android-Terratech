package com.novatech.terratech.notifications.domain.entity

import com.novatech.terratech.notifications.domain.valueobject.ReviewDetails

data class NotificationItem(
    val id: Int,
    val profileId: Int,
    val title: String,
    val message: String,
    val isRead: Boolean,
    val isAlert: Boolean,
    val type: NotificationType,
    val timestamp: String,
    val reviewDetails: ReviewDetails? = null,
)
