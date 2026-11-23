package com.novatech.terratech.inventory.domain.repository

import com.novatech.terratech.inventory.domain.entity.ProductComment
import com.novatech.terratech.inventory.domain.entity.RatingSummary

interface ProductReviewRepository {
    suspend fun comments(productId: Int, rating: Int? = null): List<ProductComment>

    suspend fun ratingSummary(productId: Int): RatingSummary
}
