package com.novatech.terratech.inventory.presentation.ui

import androidx.compose.runtime.Composable
import com.novatech.terratech.inventory.presentation.state.ProductReviewFilter
import com.novatech.terratech.inventory.presentation.state.ProductReviewsState

@Composable
fun ProductReviewsScreen(
    state: ProductReviewsState,
    onFilterSelected: (ProductReviewFilter) -> Unit,
    onRetry: () -> Unit,
) {
    ProductReviewsContent(
        state = state,
        onFilterSelected = onFilterSelected,
        onRetry = onRetry,
    )
}
