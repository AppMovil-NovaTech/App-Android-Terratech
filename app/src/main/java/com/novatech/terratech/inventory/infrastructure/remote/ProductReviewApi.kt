package com.novatech.terratech.inventory.infrastructure.remote

import com.novatech.terratech.inventory.infrastructure.remote.dto.ProductCommentDto
import com.novatech.terratech.inventory.infrastructure.remote.dto.RatingSummaryDto
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface ProductReviewApi {
    @GET("api/v1/products/{productId}/comments")
    suspend fun comments(
        @Path("productId") productId: Int,
        @Query("rating") rating: Int? = null,
    ): List<ProductCommentDto>

    @GET("api/v1/products/{productId}/ratings-summary")
    suspend fun ratingSummary(@Path("productId") productId: Int): RatingSummaryDto
}
