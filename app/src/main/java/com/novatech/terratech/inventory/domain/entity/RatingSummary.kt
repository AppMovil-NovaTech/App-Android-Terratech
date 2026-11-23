package com.novatech.terratech.inventory.domain.entity

data class RatingSummary(
    val productId: Int,
    val averageRating: Double,
    val totalReviews: Int,
    val breakdown: RatingBreakdown,
)
