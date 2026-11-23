package com.novatech.terratech.inventory.application.usecase

import com.novatech.terratech.inventory.domain.entity.ProductComment
import com.novatech.terratech.inventory.domain.repository.ProductReviewRepository

class GetProductComments(private val repository: ProductReviewRepository) {
    suspend operator fun invoke(productId: Int, rating: Int? = null): List<ProductComment> {
        require(productId > 0) { "productId must be positive" }
        require(rating == null || rating in 1..5) { "rating must be between 1 and 5" }
        return repository.comments(productId, rating)
    }
}
