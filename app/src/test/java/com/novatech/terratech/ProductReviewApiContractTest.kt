package com.novatech.terratech

import com.novatech.terratech.core.domain.Failure
import com.novatech.terratech.inventory.infrastructure.implementation.ProductReviewRepositoryImpl
import com.novatech.terratech.inventory.infrastructure.remote.ProductReviewApi
import kotlinx.coroutines.test.runTest
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.fail
import org.junit.Test
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class ProductReviewApiContractTest {
    private val comment =
        """{"id":2,"userId":2,"productId":100,"targetProfileId":10,"content":"Comentario más reciente","rating":5,"createdAt":"2026-11-21T10:00:00Z","authorName":"Mariana Ruiz"}"""

    private val summary =
        """{"productId":100,"averageRating":3.6,"totalReviews":5,"ratingBreakdown":{"oneStar":1,"twoStars":0,"threeStars":1,"fourStars":1,"fiveStars":2}}"""

    private fun api(server: MockWebServer): ProductReviewApi =
        Retrofit.Builder()
            .baseUrl(server.url("/"))
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ProductReviewApi::class.java)

    @Test
    fun commentsUseProductPathAndParseAvailableFields() = runTest {
        val server = MockWebServer()
        server.start()
        try {
            val repository = ProductReviewRepositoryImpl(api(server))
            server.enqueue(MockResponse().setBody("[$comment]"))
            val comments = repository.comments(100)
            assertEquals(1, comments.size)
            val parsed = comments.single()
            assertEquals(2, parsed.id)
            assertEquals(2, parsed.userId)
            assertEquals(100, parsed.productId)
            assertEquals(10, parsed.targetProfileId)
            assertEquals("Comentario más reciente", parsed.content)
            assertEquals(5, parsed.rating)
            assertEquals("Mariana Ruiz", parsed.authorName)
            assertEquals("2026-11-21T10:00:00Z", parsed.createdAt)
            assertEquals("/api/v1/products/100/comments", server.takeRequest().path)
        } finally {
            server.shutdown()
        }
    }

    @Test
    fun commentsUseRatingQueryWhenFilterProvided() = runTest {
        val server = MockWebServer()
        server.start()
        try {
            val repository = ProductReviewRepositoryImpl(api(server))
            server.enqueue(MockResponse().setBody("[$comment]"))
            repository.comments(100, 5)
            assertEquals("/api/v1/products/100/comments?rating=5", server.takeRequest().path)
        } finally {
            server.shutdown()
        }
    }

    @Test
    fun emptyCommentListIsAValidResponse() = runTest {
        val server = MockWebServer()
        server.start()
        try {
            val repository = ProductReviewRepositoryImpl(api(server))
            server.enqueue(MockResponse().setBody("[]"))
            val comments = repository.comments(100)
            assertEquals(0, comments.size)
        } finally {
            server.shutdown()
        }
    }

    @Test
    fun nullableCommentFieldsRemainNull() = runTest {
        val server = MockWebServer()
        server.start()
        try {
            val repository = ProductReviewRepositoryImpl(api(server))
            server.enqueue(
                MockResponse().setBody(
                    """[{"id":3,"userId":2,"productId":100,"targetProfileId":10,"content":"Sin datos","rating":null,"createdAt":null,"authorName":null}]"""
                )
            )
            val parsed = repository.comments(100).single()
            assertNull(parsed.rating)
            assertNull(parsed.createdAt)
            assertNull(parsed.authorName)
        } finally {
            server.shutdown()
        }
    }

    @Test
    fun summaryUsesProductPathAndKeepsBackendAverage() = runTest {
        val server = MockWebServer()
        server.start()
        try {
            val repository = ProductReviewRepositoryImpl(api(server))
            server.enqueue(MockResponse().setBody(summary))
            val parsed = repository.ratingSummary(100)
            assertEquals(100, parsed.productId)
            assertEquals(3.6, parsed.averageRating, 0.001)
            assertEquals(5, parsed.totalReviews)
            assertEquals(1, parsed.breakdown.oneStar)
            assertEquals(0, parsed.breakdown.twoStars)
            assertEquals(1, parsed.breakdown.threeStars)
            assertEquals(1, parsed.breakdown.fourStars)
            assertEquals(2, parsed.breakdown.fiveStars)
            assertEquals("/api/v1/products/100/ratings-summary", server.takeRequest().path)
        } finally {
            server.shutdown()
        }
    }

    @Test
    fun summaryWithoutReviewsKeepsBackendZeroValues() = runTest {
        val server = MockWebServer()
        server.start()
        try {
            val repository = ProductReviewRepositoryImpl(api(server))
            server.enqueue(
                MockResponse().setBody(
                    """{"productId":100,"averageRating":0.0,"totalReviews":0,"ratingBreakdown":{"oneStar":0,"twoStars":0,"threeStars":0,"fourStars":0,"fiveStars":0}}"""
                )
            )
            val parsed = repository.ratingSummary(100)
            assertEquals(0.0, parsed.averageRating, 0.001)
            assertEquals(0, parsed.totalReviews)
            assertEquals(0, parsed.breakdown.fiveStars)
        } finally {
            server.shutdown()
        }
    }

    @Test
    fun missingProductMapsToNotFoundFailure() = runTest {
        val server = MockWebServer()
        server.start()
        try {
            val repository = ProductReviewRepositoryImpl(api(server))
            server.enqueue(MockResponse().setResponseCode(404))
            try {
                repository.comments(999)
                fail("Expected a Failure for a missing product")
            } catch (failure: Failure) {
                assertEquals(404, failure.status)
                assertEquals("HTTP_ERROR", failure.code)
            }
        } finally {
            server.shutdown()
        }
    }

    @Test
    fun invalidRequestKeepsBackendErrorCode() = runTest {
        val server = MockWebServer()
        server.start()
        try {
            val repository = ProductReviewRepositoryImpl(api(server))
            server.enqueue(MockResponse().setResponseCode(400).setBody("""{"code":"INVALID_INPUT"}"""))
            try {
                repository.ratingSummary(100)
                fail("Expected a Failure for an invalid request")
            } catch (failure: Failure) {
                assertEquals(400, failure.status)
                assertEquals("INVALID_INPUT", failure.code)
            }
        } finally {
            server.shutdown()
        }
    }
}
