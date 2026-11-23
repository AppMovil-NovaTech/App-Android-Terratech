package com.novatech.terratech

import com.novatech.terratech.inventory.domain.entity.ProductComment
import com.novatech.terratech.inventory.domain.entity.RatingBreakdown
import com.novatech.terratech.inventory.domain.entity.RatingSummary
import com.novatech.terratech.inventory.domain.repository.ProductReviewRepository
import kotlinx.coroutines.delay

class FakeProductReviewRepository : ProductReviewRepository {
    var comments =
        listOf(
            ProductComment(
                id = 1,
                userId = 2,
                productId = 100,
                targetProfileId = 10,
                content = "Excellent harvest",
                rating = 5,
                createdAt = "2026-11-21T10:00:00Z",
                authorName = "Mariana Ruiz",
            ),
            ProductComment(
                id = 2,
                userId = 3,
                productId = 100,
                targetProfileId = 10,
                content = "Good quality",
                rating = 4,
                createdAt = null,
                authorName = null,
            ),
        )
    var summary = RatingSummary(100, 3.6, 5, RatingBreakdown(1, 0, 1, 1, 2))
    var commentFailure: Throwable? = null
    var summaryFailure: Throwable? = null
    var commentDelays: Map<Int?, Long> = emptyMap()
    val commentRequests = mutableListOf<Pair<Int, Int?>>()
    val summaryRequests = mutableListOf<Int>()

    override suspend fun comments(productId: Int, rating: Int?): List<ProductComment> {
        commentRequests += productId to rating
        commentDelays[rating]?.let { delay(it) }
        commentFailure?.let { throw it }
        return comments.filter { rating == null || it.rating == rating }
    }

    override suspend fun ratingSummary(productId: Int): RatingSummary {
        summaryRequests += productId
        summaryFailure?.let { throw it }
        return summary
    }
}
