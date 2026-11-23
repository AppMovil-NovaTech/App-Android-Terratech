package com.novatech.terratech.inventory.presentation.component

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.novatech.terratech.R

@Composable
fun ProductReviewErrorText(error: String?) {
    if (error == null) return
    val message =
        when (error) {
            "OFFLINE" -> R.string.reviews_error_offline
            "UNAUTHENTICATED" -> R.string.reviews_error_session
            "RESOURCE_NOT_FOUND", "PRODUCT_NOT_FOUND" -> R.string.reviews_error_not_found
            else -> R.string.reviews_error_generic
        }
    Text(
        stringResource(message),
        color = MaterialTheme.colorScheme.error,
        style = MaterialTheme.typography.bodyMedium,
    )
}
