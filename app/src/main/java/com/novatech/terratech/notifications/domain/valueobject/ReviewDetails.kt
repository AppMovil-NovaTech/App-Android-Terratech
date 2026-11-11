package com.novatech.terratech.notifications.domain.valueobject

data class ReviewDetails(
    val rating: Int,
    val authorName: String,
    val productName: String,
    val commentText: String,
    val isCritical: Boolean = rating <= 2,
)
