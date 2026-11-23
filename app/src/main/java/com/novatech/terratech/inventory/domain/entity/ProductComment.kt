package com.novatech.terratech.inventory.domain.entity

data class ProductComment(
    val id: Int,
    val userId: Int,
    val productId: Int,
    val targetProfileId: Int,
    val content: String,
    val rating: Int?,
    val createdAt: String?,
    val authorName: String?,
)
