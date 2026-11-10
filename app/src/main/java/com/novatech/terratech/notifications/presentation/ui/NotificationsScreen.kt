package com.novatech.terratech.notifications.presentation.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.novatech.terratech.R
import com.novatech.terratech.notifications.domain.entity.NotificationItem
import com.novatech.terratech.notifications.presentation.viewmodel.NotificationsViewModel

@Composable
fun NotificationsScreen(
    userId: Int,
    profileId: Int,
    viewModel: NotificationsViewModel,
    onNavigateToProduct: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    var replyingItem by remember { mutableStateOf<NotificationItem?>(null) }
    var replyText by rememberSaveable { mutableStateOf("") }

    LaunchedEffect(userId, profileId) {
        viewModel.load(userId, profileId)
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        snackbarHost = { SnackbarHost(snackbarHostState) },
    ) { padding ->
        Box(modifier = Modifier.padding(padding)) {
            NotificationsContent(
                state = state,
                onFilterSelected = viewModel::setFilter,
                onNotificationClick = { item ->
                    if (!item.isRead) {
                        viewModel.markAsRead(item.id)
                    }
                },
                onViewReview = { item ->
                    if (!item.isRead) {
                        viewModel.markAsRead(item.id)
                    }
                    val product = item.reviewDetails?.productName.orEmpty()
                    onNavigateToProduct(product)
                },
                onReplyReview = { item ->
                    if (!item.isRead) {
                        viewModel.markAsRead(item.id)
                    }
                    replyingItem = item
                    replyText = ""
                },
            )
        }
    }

    replyingItem?.let { item ->
        AlertDialog(
            onDismissRequest = { replyingItem = null },
            title = { Text(stringResource(R.string.notification_reply) + " a " + (item.reviewDetails?.authorName ?: "Agricultor")) },
            text = {
                OutlinedTextField(
                    value = replyText,
                    onValueChange = { replyText = it },
                    label = { Text("Escribe tu respuesta...") },
                    singleLine = false,
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        replyingItem = null
                    },
                ) {
                    Text("Enviar")
                }
            },
            dismissButton = {
                TextButton(onClick = { replyingItem = null }) {
                    Text("Cancelar")
                }
            },
        )
    }
}
