package com.novatech.terratech.inventory.presentation.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.novatech.terratech.R
import com.novatech.terratech.inventory.presentation.state.ProductReviewFilter

@Composable
fun ProductReviewFilterChips(
    selected: ProductReviewFilter,
    onSelected: (ProductReviewFilter) -> Unit,
) {
    LazyRow(
        contentPadding = PaddingValues(horizontal = 2.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        items(ProductReviewFilter.entries) { filter ->
            FilterChip(
                selected = filter == selected,
                onClick = { onSelected(filter) },
                label = {
                    Text(
                        when (filter) {
                            ProductReviewFilter.ALL -> stringResource(R.string.reviews_filter_all)
                            else -> {
                                val stars = filter.stars ?: 0
                                pluralStringResource(R.plurals.reviews_star_count, stars, stars)
                            }
                        }
                    )
                },
            )
        }
    }
}
