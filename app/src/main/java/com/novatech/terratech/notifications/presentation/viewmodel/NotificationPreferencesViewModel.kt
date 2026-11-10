package com.novatech.terratech.notifications.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.novatech.terratech.notifications.application.usecase.NotificationActions
import com.novatech.terratech.notifications.domain.entity.NotificationPreferences
import com.novatech.terratech.notifications.presentation.state.NotificationPreferencesState
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class NotificationPreferencesViewModel @Inject constructor(
    private val actions: NotificationActions,
) : ViewModel() {

    private val _state = MutableStateFlow(NotificationPreferencesState())
    val state = _state.asStateFlow()

    fun load(userId: Int) {
        viewModelScope.launch {
            _state.update { it.copy(userId = userId, busy = true, error = null) }
            try {
                val preferences = actions.getPreferences(userId)
                _state.update { it.copy(preferences = preferences, busy = false) }
            } catch (e: Exception) {
                _state.update { it.copy(busy = false, error = e.localizedMessage) }
            }
        }
    }

    fun updatePreferences(transform: (NotificationPreferences) -> NotificationPreferences) {
        val updated = transform(_state.value.preferences)
        _state.update { it.copy(preferences = updated) }
        save(updated)
    }

    private fun save(preferences: NotificationPreferences) {
        viewModelScope.launch {
            try {
                actions.savePreferences(preferences)
                _state.update { it.copy(message = "Preferencias actualizadas") }
            } catch (e: Exception) {
                _state.update { it.copy(error = e.localizedMessage) }
            }
        }
    }

    fun clearMessage() {
        _state.update { it.copy(message = null, error = null) }
    }
}
