package com.novatech.terratech

import com.novatech.terratech.core.domain.Failure
import com.novatech.terratech.inventory.application.usecase.GetProductComments
import com.novatech.terratech.inventory.application.usecase.GetProductRatingSummary
import com.novatech.terratech.inventory.domain.entity.RatingBreakdown
import com.novatech.terratech.inventory.domain.entity.RatingSummary
import com.novatech.terratech.inventory.presentation.state.ProductReviewFilter
import com.novatech.terratech.inventory.presentation.viewmodel.ProductReviewsViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class ProductReviewsViewModelTest {
    private val dispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(dispatcher)
    }

    @After
    fun teardown() {
        Dispatchers.resetMain()
    }

    @Test
    fun initialLoadFetchesSummaryAndCommentsForProduct() = runTest {
        val repository = FakeProductReviewRepository()
        val viewModel = viewModel(repository)
        viewModel.load(100, "Papa amarilla")
        advanceUntilIdle()
        val state = viewModel.state.value
        assertEquals(100, state.productId)
        assertEquals("Papa amarilla", state.productName)
        assertEquals(5, state.summary!!.totalReviews)
        assertEquals(2, state.comments.size)
        assertFalse(state.loadingSummary)
        assertFalse(state.loadingComments)
        assertNull(state.summaryError)
        assertNull(state.commentsError)
        assertEquals(listOf(100 to null), repository.commentRequests)
        assertEquals(listOf(100), repository.summaryRequests)
    }

    @Test
    fun changingFilterRequestsStarsAndKeepsSummary() = runTest {
        val repository = FakeProductReviewRepository()
        val viewModel = viewModel(repository)
        viewModel.load(100, "Papa amarilla")
        advanceUntilIdle()
        viewModel.selectFilter(ProductReviewFilter.FIVE_STARS)
        advanceUntilIdle()
        val state = viewModel.state.value
        assertEquals(ProductReviewFilter.FIVE_STARS, state.filter)
        assertEquals(5, state.summary!!.totalReviews)
        assertEquals(1, state.comments.size)
        assertTrue(state.comments.all { it.rating == 5 })
        assertEquals(100 to 5, repository.commentRequests.last())
    }

    @Test
    fun emptyCommentListIsPreservedAsEmptyState() = runTest {
        val repository = FakeProductReviewRepository()
        repository.comments = emptyList()
        val viewModel = viewModel(repository)
        viewModel.load(100, "Papa amarilla")
        advanceUntilIdle()
        val state = viewModel.state.value
        assertTrue(state.comments.isEmpty())
        assertFalse(state.loadingComments)
        assertNull(state.commentsError)
    }

    @Test
    fun productWithoutRatingsKeepsBackendZeroSummary() = runTest {
        val repository = FakeProductReviewRepository()
        repository.comments = emptyList()
        repository.summary = RatingSummary(100, 0.0, 0, RatingBreakdown(0, 0, 0, 0, 0))
        val viewModel = viewModel(repository)
        viewModel.load(100, "Papa amarilla")
        advanceUntilIdle()
        val state = viewModel.state.value
        val summary = state.summary ?: error("summary was not loaded")
        assertEquals(0, summary.totalReviews)
        assertEquals(0.0, summary.averageRating, 0.001)
        assertTrue(state.comments.isEmpty())
    }

    @Test
    fun commentErrorIsExposedAndRetryLoadsAgain() = runTest {
        val repository = FakeProductReviewRepository()
        repository.commentFailure = Failure("OFFLINE")
        val viewModel = viewModel(repository)
        viewModel.load(100, "Papa amarilla")
        advanceUntilIdle()
        assertEquals("OFFLINE", viewModel.state.value.commentsError)
        repository.commentFailure = null
        viewModel.retry()
        advanceUntilIdle()
        val state = viewModel.state.value
        assertNull(state.commentsError)
        assertEquals(2, state.comments.size)
    }

    @Test
    fun missingProductMapsToResourceNotFound() = runTest {
        val repository = FakeProductReviewRepository()
        repository.summaryFailure = Failure("HTTP_ERROR", 404)
        repository.commentFailure = Failure("HTTP_ERROR", 404)
        val viewModel = viewModel(repository)
        viewModel.load(100, "Papa amarilla")
        advanceUntilIdle()
        val state = viewModel.state.value
        assertEquals("RESOURCE_NOT_FOUND", state.summaryError)
        assertEquals("RESOURCE_NOT_FOUND", state.commentsError)
    }

    @Test
    fun rapidFilterChangesKeepOnlyLatestResponse() = runTest {
        val repository = FakeProductReviewRepository()
        repository.commentDelays = mapOf(5 to 100L, 4 to 10L)
        val viewModel = viewModel(repository)
        viewModel.load(100, "Papa amarilla")
        advanceUntilIdle()
        viewModel.selectFilter(ProductReviewFilter.FIVE_STARS)
        viewModel.selectFilter(ProductReviewFilter.FOUR_STARS)
        advanceUntilIdle()
        val state = viewModel.state.value
        assertEquals(ProductReviewFilter.FOUR_STARS, state.filter)
        assertEquals(1, state.comments.size)
        assertTrue(state.comments.all { it.rating == 4 })
        assertFalse(state.loadingComments)
        assertNull(state.commentsError)
    }

    @Test
    fun loadingSameProductTwiceDoesNotDuplicateRequests() = runTest {
        val repository = FakeProductReviewRepository()
        val viewModel = viewModel(repository)
        viewModel.load(100, "Papa amarilla")
        advanceUntilIdle()
        val commentRequestCount = repository.commentRequests.size
        val summaryRequestCount = repository.summaryRequests.size
        viewModel.load(100, "Papa amarilla")
        advanceUntilIdle()
        assertEquals(commentRequestCount, repository.commentRequests.size)
        assertEquals(summaryRequestCount, repository.summaryRequests.size)
    }

    private fun viewModel(repository: FakeProductReviewRepository): ProductReviewsViewModel =
        ProductReviewsViewModel(
            GetProductComments(repository),
            GetProductRatingSummary(repository),
        )
}
