package com.novatech.terratech.notifications.presentation.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.novatech.terratech.core.presentation.navigation.AppNavigator
import com.novatech.terratech.core.presentation.navigation.TopLevelDestination
import com.novatech.terratech.iam.domain.entity.Session
import com.novatech.terratech.notifications.presentation.ui.NotificationsScreen
import com.novatech.terratech.notifications.presentation.viewmodel.NotificationsViewModel
import com.novatech.terratech.profile.presentation.state.ProfileState

fun NavGraphBuilder.notificationsDestination(
    navigator: AppNavigator,
    session: Session,
    profileState: ProfileState,
    viewModel: NotificationsViewModel,
) {
    composable<NotificationsDestination> {
        val profileId = profileState.profile?.id ?: 1
        NotificationsScreen(
            userId = session.userId,
            profileId = profileId,
            viewModel = viewModel,
            onNavigateToProduct = {
                navigator.topLevel(TopLevelDestination.INVENTORY)
            },
        )
    }
}
