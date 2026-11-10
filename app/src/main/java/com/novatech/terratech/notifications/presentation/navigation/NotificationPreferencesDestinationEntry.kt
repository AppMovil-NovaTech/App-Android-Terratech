package com.novatech.terratech.notifications.presentation.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.novatech.terratech.core.presentation.navigation.AppNavigator
import com.novatech.terratech.iam.domain.entity.Session
import com.novatech.terratech.notifications.presentation.ui.NotificationPreferencesScreen
import com.novatech.terratech.notifications.presentation.viewmodel.NotificationPreferencesViewModel

fun NavGraphBuilder.notificationPreferencesDestination(
    session: Session,
    viewModel: NotificationPreferencesViewModel,
) {
    composable<NotificationPreferencesDestination> {
        NotificationPreferencesScreen(
            userId = session.userId,
            viewModel = viewModel,
        )
    }
}
