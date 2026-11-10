package com.novatech.terratech.notifications.presentation.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.navigation
import com.novatech.terratech.core.presentation.navigation.AppNavigator
import com.novatech.terratech.iam.domain.entity.Session
import com.novatech.terratech.notifications.presentation.viewmodel.NotificationPreferencesViewModel
import com.novatech.terratech.notifications.presentation.viewmodel.NotificationsViewModel
import com.novatech.terratech.profile.presentation.state.ProfileState

fun NavGraphBuilder.notificationsGraph(
    navigator: AppNavigator,
    session: Session,
    profileState: ProfileState,
    notificationsViewModel: NotificationsViewModel,
    preferencesViewModel: NotificationPreferencesViewModel,
) {
    navigation<NotificationsGraph>(startDestination = NotificationsDestination) {
        notificationsDestination(navigator, session, profileState, notificationsViewModel)
        notificationPreferencesDestination(session, preferencesViewModel)
    }
}
