package com.novatech.terratech.inventory.presentation.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.novatech.terratech.R
import com.novatech.terratech.core.presentation.component.EmptyCard
import com.novatech.terratech.core.presentation.component.PageTitle
import com.novatech.terratech.inventory.presentation.component.ProductReviewCommentCard
import com.novatech.terratech.inventory.presentation.component.ProductReviewErrorText
import com.novatech.terratech.inventory.presentation.component.ProductReviewFilterChips
import com.novatech.terratech.inventory.presentation.component.ProductReviewSummaryCard
import com.novatech.terratech.inventory.presentation.state.ProductReviewFilter
import com.novatech.terratech.inventory.presentation.state.ProductReviewsState

@Composable
fun ProductReviewsContent(
    state: ProductReviewsState,
    onFilterSelected: (ProductReviewFilter) -> Unit,
    onRetry: () -> Unit,
) {
    val summary = state.summary
    val summaryError = state.summaryError
    val commentsError = state.commentsError
    LazyColumn(
        Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        item {
            PageTitle(
                title = state.productName.ifBlank { stringResource(R.string.reviews_title) },
                subtitle = stringResource(R.string.reviews_subtitle),
            )
        }
        when {
            state.loadingSummary && summary == null -> item { CircularProgressIndicator() }
            summaryError != null ->
                item {
                    ProductReviewErrorText(summaryError)
                    OutlinedButton(onClick = onRetry) { Text(stringResource(R.string.reviews_retry)) }
                }
            summary != null -> {
                item { ProductReviewSummaryCard(summary) }
                if (summary.totalReviews > 0) {
                    item { ProductReviewFilterChips(state.filter, onFilterSelected) }
                    when {
                        state.loadingComments -> item { CircularProgressIndicator() }
                        commentsError != null ->
                            item {
                                ProductReviewErrorText(commentsError)
                                OutlinedButton(onClick = onRetry) { Text(stringResource(R.string.reviews_retry)) }
                            }
                        state.comments.isEmpty() ->
                            item {
                                if (state.filter == ProductReviewFilter.ALL) {
                                    EmptyCard(
                                        stringResource(R.string.reviews_no_comments),
                                        stringResource(R.string.reviews_no_comments_help),
                                    )
                                } else {
                                    EmptyCard(
                                        stringResource(R.string.reviews_filtered_empty, state.filter.stars ?: 0),
                                        stringResource(R.string.reviews_filtered_empty_help),
                                    )
                                }
                            }
                        else ->
                            items(state.comments, key = { it.id }) { comment ->
                                ProductReviewCommentCard(comment)
                            }
                    }
                }
            }
        }
    }
}
