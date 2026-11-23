package com.novatech.terratech.inventory.presentation.navigation

import kotlinx.serialization.Serializable

@Serializable
data class ProductReviewsDestination(
    val productId: Int,
    val productName: String,
)
