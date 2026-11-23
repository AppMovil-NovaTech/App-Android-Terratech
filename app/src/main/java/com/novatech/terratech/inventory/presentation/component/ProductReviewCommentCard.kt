package com.novatech.terratech.inventory.presentation.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.novatech.terratech.core.presentation.component.FarmCard
import com.novatech.terratech.inventory.domain.entity.ProductComment
import com.novatech.terratech.inventory.presentation.format.reviewDateText
import com.novatech.terratech.ui.theme.Muted

@Composable
fun ProductReviewCommentCard(comment: ProductComment) {
    FarmCard {
        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            comment.authorName?.takeIf { it.isNotBlank() }?.let {
                Text(it, style = MaterialTheme.typography.titleSmall)
            }
            comment.rating?.let { StarRating(it) }
        }
        comment.createdAt?.takeIf { it.isNotBlank() }?.let {
            Text(reviewDateText(it), color = Muted, style = MaterialTheme.typography.bodySmall)
        }
        Text(comment.content, style = MaterialTheme.typography.bodyMedium)
    }
}
