package com.novatech.terratech

import com.novatech.terratech.inventory.application.usecase.GetProductComments
import com.novatech.terratech.inventory.application.usecase.GetProductRatingSummary
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

class ProductReviewUseCaseTest {
    @Test
    fun commentsPassProductAndRatingToRepository() = runTest {
        val repository = FakeProductReviewRepository()
        val comments = GetProductComments(repository)(100, 5)
        assertEquals(1, comments.size)
        assertEquals(listOf(100 to 5), repository.commentRequests)
    }

    @Test
    fun summaryPassesProductToRepository() = runTest {
        val repository = FakeProductReviewRepository()
        val summary = GetProductRatingSummary(repository)(100)
        assertEquals(100, summary.productId)
        assertEquals(listOf(100), repository.summaryRequests)
    }

    @Test(expected = IllegalArgumentException::class)
    fun commentsRejectNonPositiveProductId() = runTest {
        GetProductComments(FakeProductReviewRepository())(0)
    }

    @Test(expected = IllegalArgumentException::class)
    fun commentsRejectRatingOutsideOneToFive() = runTest {
        GetProductComments(FakeProductReviewRepository())(100, 6)
    }

    @Test(expected = IllegalArgumentException::class)
    fun summaryRejectsNonPositiveProductId() = runTest {
        GetProductRatingSummary(FakeProductReviewRepository())(0)
    }
}
