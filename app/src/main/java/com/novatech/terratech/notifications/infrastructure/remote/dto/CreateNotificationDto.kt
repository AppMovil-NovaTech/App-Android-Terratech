package com.novatech.terratech.notifications.infrastructure.remote.dto

data class CreateNotificationDto(
    val profileId: Int,
    val title: String,
    val message: String,
    val isAlert: Boolean = false,
)
