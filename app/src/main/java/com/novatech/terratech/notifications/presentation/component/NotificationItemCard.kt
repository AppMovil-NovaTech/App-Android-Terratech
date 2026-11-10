package com.novatech.terratech.notifications.presentation.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.novatech.terratech.R
import com.novatech.terratech.notifications.domain.entity.NotificationItem
import com.novatech.terratech.notifications.domain.entity.NotificationType

@Composable
fun NotificationItemCard(
    item: NotificationItem,
    onClick: () -> Unit,
    onViewReview: (NotificationItem) -> Unit,
    onReplyReview: (NotificationItem) -> Unit,
    modifier: Modifier = Modifier,
) {
    val isCriticalReview = item.type == NotificationType.REVIEW && item.reviewDetails?.isCritical == true
    val cardBorder = if (isCriticalReview) {
        Modifier.border(1.5.dp, MaterialTheme.colorScheme.error, RoundedCornerShape(16.dp))
    } else {
        Modifier
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .then(cardBorder)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (item.isRead) Color.White else Color(0xFFF7FBF8)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = if (item.isRead) 1.dp else 2.dp),
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                if (!item.isRead) {
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .background(Color(0xFF2196F3), CircleShape)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                }

                Text(
                    text = item.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = if (item.isRead) FontWeight.Normal else FontWeight.Bold,
                    color = if (isCriticalReview) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.weight(1f),
                )

                Text(
                    text = item.timestamp,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.outline,
                )
            }

            Text(
                text = item.message,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )

            if (item.type == NotificationType.REVIEW && item.reviewDetails != null) {
                val rating = item.reviewDetails.rating
                val stars = "★".repeat(rating) + "☆".repeat(5 - rating)

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = stars,
                        color = if (item.reviewDetails.isCritical) MaterialTheme.colorScheme.error else Color(0xFFF59E0B),
                        style = MaterialTheme.typography.titleMedium,
                    )

                    if (item.reviewDetails.isCritical) {
                        OutlinedButton(
                            onClick = { onReplyReview(item) },
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = MaterialTheme.colorScheme.error
                            ),
                        ) {
                            Text(stringResource(R.string.notification_reply))
                        }
                    } else {
                        OutlinedButton(
                            onClick = { onViewReview(item) },
                        ) {
                            Text(stringResource(R.string.notification_view_review))
                        }
                    }
                }
            }
        }
    }
}
