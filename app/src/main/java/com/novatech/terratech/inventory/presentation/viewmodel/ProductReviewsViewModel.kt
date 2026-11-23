package com.novatech.terratech.inventory.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.novatech.terratech.core.domain.Failure
import com.novatech.terratech.inventory.application.usecase.GetProductComments
import com.novatech.terratech.inventory.application.usecase.GetProductRatingSummary
import com.novatech.terratech.inventory.presentation.state.ProductReviewFilter
import com.novatech.terratech.inventory.presentation.state.ProductReviewsState
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class ProductReviewsViewModel @Inject constructor(
    private val getComments: GetProductComments,
    private val getSummary: GetProductRatingSummary,
) : ViewModel() {
    private val mutable = MutableStateFlow(ProductReviewsState())
    val state = mutable.asStateFlow()
    private var loadedProductId: Int = 0
    private var loadedProductName: String = ""
    private var commentsRequest: Job? = null
    private var summaryRequest: Job? = null

    fun load(productId: Int, productName: String) {
        if (productId == loadedProductId && productName == loadedProductName) return
        loadedProductId = productId
        loadedProductName = productName
        commentsRequest?.cancel()
        summaryRequest?.cancel()
        mutable.value = ProductReviewsState(productId = productId, productName = productName)
        loadSummary(productId)
        loadComments(productId, ProductReviewFilter.ALL)
    }

    fun selectFilter(filter: ProductReviewFilter) {
        if (mutable.value.filter == filter) return
        mutable.update { it.copy(filter = filter) }
        loadComments(loadedProductId, filter)
    }

    fun retry() {
        if (loadedProductId <= 0) return
        if (mutable.value.summary == null) loadSummary(loadedProductId)
        loadComments(loadedProductId, mutable.value.filter)
    }

    private fun loadSummary(productId: Int) {
        summaryRequest?.cancel()
        mutable.update { it.copy(loadingSummary = true, summaryError = null) }
        summaryRequest =
            viewModelScope.launch {
                try {
                    val summary = getSummary(productId)
                    if (loadedProductId == productId) {
                        mutable.update { it.copy(summary = summary, loadingSummary = false) }
                    }
                } catch (exception: CancellationException) {
                    throw exception
                } catch (exception: Exception) {
                    if (loadedProductId == productId) {
                        mutable.update {
                            it.copy(
                                loadingSummary = false,
                                summaryError = exception.errorCode(),
                            )
                        }
                    }
                }
            }
    }

    private fun loadComments(productId: Int, filter: ProductReviewFilter) {
        commentsRequest?.cancel()
        mutable.update {
            it.copy(
                comments = emptyList(),
                loadingComments = true,
                commentsError = null,
            )
        }
        commentsRequest =
            viewModelScope.launch {
                try {
                    val comments = getComments(productId, filter.stars)
                    if (loadedProductId == productId && mutable.value.filter == filter) {
                        mutable.update {
                            it.copy(
                                comments = comments,
                                loadingComments = false,
                            )
                        }
                    }
                } catch (exception: CancellationException) {
                    throw exception
                } catch (exception: Exception) {
                    if (loadedProductId == productId && mutable.value.filter == filter) {
                        mutable.update {
                            it.copy(
                                loadingComments = false,
                                commentsError = exception.errorCode(),
                            )
                        }
                    }
                }
            }
    }

    private fun Exception.errorCode(): String {
        val failure = this as? Failure ?: return "REVIEWS_ERROR"
        return when (failure.status) {
            401 -> "UNAUTHENTICATED"
            404 -> "RESOURCE_NOT_FOUND"
            else -> failure.code
        }
    }
}
