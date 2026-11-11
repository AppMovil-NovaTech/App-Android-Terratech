package com.novatech.terratech.notifications.presentation.component

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.novatech.terratech.R
import com.novatech.terratech.notifications.presentation.state.NotificationFilter

@Composable
fun NotificationFilterChips(
    selectedFilter: NotificationFilter,
    onFilterSelected: (NotificationFilter) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState())
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        FilterChip(
            selected = selectedFilter == NotificationFilter.ALL,
            onClick = { onFilterSelected(NotificationFilter.ALL) },
            label = { Text(stringResource(R.string.notifications_filter_all)) },
        )
        FilterChip(
            selected = selectedFilter == NotificationFilter.UNREAD,
            onClick = { onFilterSelected(NotificationFilter.UNREAD) },
            label = { Text(stringResource(R.string.notifications_filter_unread)) },
        )
        FilterChip(
            selected = selectedFilter == NotificationFilter.ALERTS,
            onClick = { onFilterSelected(NotificationFilter.ALERTS) },
            label = { Text(stringResource(R.string.notifications_filter_alerts)) },
        )
        FilterChip(
            selected = selectedFilter == NotificationFilter.REVIEWS,
            onClick = { onFilterSelected(NotificationFilter.REVIEWS) },
            label = { Text(stringResource(R.string.notifications_filter_reviews)) },
        )
    }
}
