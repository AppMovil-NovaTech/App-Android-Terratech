package com.novatech.terratech.notifications.presentation.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.novatech.terratech.R
import com.novatech.terratech.notifications.domain.entity.NotificationItem
import com.novatech.terratech.notifications.presentation.component.NotificationFilterChips
import com.novatech.terratech.notifications.presentation.component.NotificationItemCard
import com.novatech.terratech.notifications.presentation.state.NotificationFilter
import com.novatech.terratech.notifications.presentation.state.NotificationsState

@Composable
fun NotificationsContent(
    state: NotificationsState,
    onFilterSelected: (NotificationFilter) -> Unit,
    onNotificationClick: (NotificationItem) -> Unit,
    onViewReview: (NotificationItem) -> Unit,
    onReplyReview: (NotificationItem) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxSize(),
    ) {
        NotificationFilterChips(
            selectedFilter = state.filter,
            onFilterSelected = onFilterSelected,
        )

        val items = state.filteredItems

        if (items.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(32.dp),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = stringResource(R.string.notifications_empty),
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.outline,
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                items(
                    items = items,
                    key = { it.id },
                ) { item ->
                    NotificationItemCard(
                        item = item,
                        onClick = { onNotificationClick(item) },
                        onViewReview = onViewReview,
                        onReplyReview = onReplyReview,
                    )
                }
            }
        }
    }
}
