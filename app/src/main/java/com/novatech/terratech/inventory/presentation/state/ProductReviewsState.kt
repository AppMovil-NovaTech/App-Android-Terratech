package com.novatech.terratech.inventory.presentation.state

import com.novatech.terratech.inventory.domain.entity.ProductComment
import com.novatech.terratech.inventory.domain.entity.RatingSummary

data class ProductReviewsState(
    val productId: Int = 0,
    val productName: String = "",
    val summary: RatingSummary? = null,
    val comments: List<ProductComment> = emptyList(),
    val filter: ProductReviewFilter = ProductReviewFilter.ALL,
    val loadingSummary: Boolean = false,
    val loadingComments: Boolean = false,
    val summaryError: String? = null,
    val commentsError: String? = null,
)
