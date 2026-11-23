package com.novatech.terratech.inventory.application.usecase

import com.novatech.terratech.inventory.domain.entity.RatingSummary
import com.novatech.terratech.inventory.domain.repository.ProductReviewRepository

class GetProductRatingSummary(private val repository: ProductReviewRepository) {
    suspend operator fun invoke(productId: Int): RatingSummary {
        require(productId > 0) { "productId must be positive" }
        return repository.ratingSummary(productId)
    }
}
