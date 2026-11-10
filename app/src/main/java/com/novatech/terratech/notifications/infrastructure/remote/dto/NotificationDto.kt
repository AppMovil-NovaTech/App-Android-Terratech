package com.novatech.terratech.notifications.infrastructure.remote.dto

data class NotificationDto(
    val id: Int,
    val profileId: Int,
    val title: String,
    val message: String,
    val isRead: Boolean,
    val isAlert: Boolean,
    val createdAt: String? = null,
)
