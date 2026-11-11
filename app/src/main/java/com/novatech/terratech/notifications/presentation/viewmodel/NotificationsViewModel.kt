package com.novatech.terratech.notifications.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.novatech.terratech.notifications.application.usecase.NotificationActions
import com.novatech.terratech.notifications.presentation.state.NotificationFilter
import com.novatech.terratech.notifications.presentation.state.NotificationsState
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class NotificationsViewModel @Inject constructor(
    private val actions: NotificationActions,
) : ViewModel() {

    private val _state = MutableStateFlow(NotificationsState())
    val state = _state.asStateFlow()

    fun load(userId: Int, profileId: Int) {
        viewModelScope.launch {
            _state.update { it.copy(userId = userId, profileId = profileId, busy = true, error = null) }
            try {
                val items = actions.getNotifications(userId, profileId)
                _state.update { it.copy(items = items, busy = false) }
            } catch (e: Exception) {
                _state.update { it.copy(busy = false, error = e.localizedMessage) }
            }
        }
    }

    fun markAsRead(notificationId: Int) {
        viewModelScope.launch {
            actions.markAsRead(notificationId)
            _state.update { current ->
                val updated = current.items.map { item ->
                    if (item.id == notificationId) item.copy(isRead = true) else item
                }
                current.copy(items = updated)
            }
        }
    }

    fun setFilter(filter: NotificationFilter) {
        _state.update { it.copy(filter = filter) }
    }

    fun clearMessage() {
        _state.update { it.copy(message = null, error = null) }
    }
}
