package com.novatech.terratech.inventory.presentation.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import com.novatech.terratech.R
import com.novatech.terratech.core.presentation.component.FarmCard
import com.novatech.terratech.inventory.domain.entity.RatingSummary
import com.novatech.terratech.ui.theme.Muted
import kotlin.math.roundToInt

@Composable
fun ProductReviewSummaryCard(summary: RatingSummary) {
    FarmCard {
        if (summary.totalReviews == 0) {
            Text(stringResource(R.string.reviews_no_ratings), style = MaterialTheme.typography.titleMedium)
            Text(stringResource(R.string.reviews_no_ratings_help), color = Muted)
        } else {
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Column {
                    Text(
                        stringResource(R.string.reviews_average_value, summary.averageRating),
                        style = MaterialTheme.typography.headlineMedium,
                    )
                    StarRating(summary.averageRating.roundToInt())
                }
                Text(
                    pluralStringResource(
                        R.plurals.reviews_total,
                        summary.totalReviews,
                        summary.totalReviews,
                    ),
                    color = Muted,
                )
            }
            Text(stringResource(R.string.reviews_breakdown_title), style = MaterialTheme.typography.titleSmall)
            for (stars in 5 downTo 1) {
                val count =
                    when (stars) {
                        5 -> summary.breakdown.fiveStars
                        4 -> summary.breakdown.fourStars
                        3 -> summary.breakdown.threeStars
                        2 -> summary.breakdown.twoStars
                        else -> summary.breakdown.oneStar
                    }
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text(
                        pluralStringResource(R.plurals.reviews_star_count, stars, stars),
                        color = Muted,
                        style = MaterialTheme.typography.bodySmall,
                    )
                    Text(count.toString(), style = MaterialTheme.typography.bodySmall)
                }
            }
        }
    }
}
