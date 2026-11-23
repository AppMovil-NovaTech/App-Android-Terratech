package com.novatech.terratech.inventory.infrastructure.remote.dto

data class RatingSummaryDto(
    val productId: Int,
    val averageRating: Double,
    val totalReviews: Int,
    val ratingBreakdown: RatingBreakdownDto,
)
