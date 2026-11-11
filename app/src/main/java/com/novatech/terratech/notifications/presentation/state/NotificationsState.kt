package com.novatech.terratech.notifications.presentation.state

import com.novatech.terratech.notifications.domain.entity.NotificationItem
import com.novatech.terratech.notifications.domain.entity.NotificationType

data class NotificationsState(
    val userId: Int = 0,
    val profileId: Int = 0,
    val items: List<NotificationItem> = emptyList(),
    val filter: NotificationFilter = NotificationFilter.ALL,
    val busy: Boolean = false,
    val error: String? = null,
    val message: String? = null,
) {
    val unreadCount: Int get() = items.count { !it.isRead }

    val filteredItems: List<NotificationItem>
        get() {
            val base = when (filter) {
                NotificationFilter.ALL -> items
                NotificationFilter.UNREAD -> items.filter { !it.isRead }
                NotificationFilter.ALERTS -> items.filter { it.isAlert }
                NotificationFilter.REVIEWS -> items.filter { it.type == NotificationType.REVIEW }
            }
            return base.sortedWith(compareBy({ it.isRead }, { it.id }))
        }
}
