package com.novatech.terratech.inventory.infrastructure.mapper

import com.novatech.terratech.inventory.domain.entity.ProductComment
import com.novatech.terratech.inventory.domain.entity.RatingBreakdown
import com.novatech.terratech.inventory.domain.entity.RatingSummary
import com.novatech.terratech.inventory.infrastructure.remote.dto.ProductCommentDto
import com.novatech.terratech.inventory.infrastructure.remote.dto.RatingSummaryDto

fun ProductCommentDto.domain(): ProductComment =
    ProductComment(
        id = id,
        userId = userId,
        productId = productId,
        targetProfileId = targetProfileId,
        content = content,
        rating = rating,
        createdAt = createdAt,
        authorName = authorName,
    )

fun RatingSummaryDto.domain(): RatingSummary =
    RatingSummary(
        productId = productId,
        averageRating = averageRating,
        totalReviews = totalReviews,
        breakdown =
            RatingBreakdown(
                oneStar = ratingBreakdown.oneStar,
                twoStars = ratingBreakdown.twoStars,
                threeStars = ratingBreakdown.threeStars,
                fourStars = ratingBreakdown.fourStars,
                fiveStars = ratingBreakdown.fiveStars,
            ),
    )
