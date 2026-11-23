package com.novatech.terratech.inventory.infrastructure.remote.dto

data class ProductCommentDto(
    val id: Int,
    val userId: Int,
    val productId: Int,
    val targetProfileId: Int,
    val content: String,
    val rating: Int?,
    val createdAt: String?,
    val authorName: String?,
)
