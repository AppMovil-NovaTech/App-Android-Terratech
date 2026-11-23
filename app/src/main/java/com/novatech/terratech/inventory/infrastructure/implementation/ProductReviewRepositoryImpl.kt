package com.novatech.terratech.inventory.infrastructure.implementation

import com.novatech.terratech.core.infrastructure.remote.apiCall
import com.novatech.terratech.inventory.domain.entity.ProductComment
import com.novatech.terratech.inventory.domain.entity.RatingSummary
import com.novatech.terratech.inventory.domain.repository.ProductReviewRepository
import com.novatech.terratech.inventory.infrastructure.mapper.domain
import com.novatech.terratech.inventory.infrastructure.remote.ProductReviewApi

class ProductReviewRepositoryImpl(private val api: ProductReviewApi) : ProductReviewRepository {
    override suspend fun comments(productId: Int, rating: Int?): List<ProductComment> =
        apiCall { api.comments(productId, rating) }.map { it.domain() }

    override suspend fun ratingSummary(productId: Int): RatingSummary =
        apiCall { api.ratingSummary(productId) }.domain()
}
