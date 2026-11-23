package com.novatech.terratech.inventory.presentation.navigation

import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.novatech.terratech.inventory.presentation.ui.ProductReviewsScreen
import com.novatech.terratech.inventory.presentation.viewmodel.ProductReviewsViewModel

fun NavGraphBuilder.productReviewsDestination() {
    composable<ProductReviewsDestination> { entry ->
        val destination = entry.toRoute<ProductReviewsDestination>()
        val viewModel: ProductReviewsViewModel = hiltViewModel()
        val state by viewModel.state.collectAsStateWithLifecycle()
        LaunchedEffect(destination.productId, destination.productName) {
            viewModel.load(destination.productId, destination.productName)
        }
        ProductReviewsScreen(
            state = state,
            onFilterSelected = viewModel::selectFilter,
            onRetry = viewModel::retry,
        )
    }
}
