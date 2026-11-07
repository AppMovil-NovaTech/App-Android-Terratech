package com.novatech.terratech.core.presentation.ui

import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.novatech.terratech.core.presentation.navigation.AppCreationEffect
import com.novatech.terratech.core.presentation.navigation.AppDestination
import com.novatech.terratech.core.presentation.navigation.AppNavigator
import com.novatech.terratech.core.presentation.navigation.TopLevelDestination
import com.novatech.terratech.iam.domain.entity.Session
import com.novatech.terratech.iam.presentation.state.AccountState
import com.novatech.terratech.iam.presentation.ui.ReauthenticationRoute
import com.novatech.terratech.iam.presentation.viewmodel.AccountViewModel
import com.novatech.terratech.inventory.presentation.navigation.InventoryNavigationActions
import com.novatech.terratech.inventory.presentation.state.InventoryState
import com.novatech.terratech.inventory.presentation.viewmodel.InventoryViewModel
import com.novatech.terratech.monitoring.presentation.navigation.navigationActions
import com.novatech.terratech.monitoring.presentation.state.MonitoringState
import com.novatech.terratech.monitoring.presentation.viewmodel.MonitoringViewModel
import com.novatech.terratech.profile.presentation.navigation.ProfileNavigationActions
import com.novatech.terratech.profile.presentation.state.ProfileState
import com.novatech.terratech.profile.presentation.viewmodel.ProfileViewModel

@Composable
internal fun SignedInApp(
    session: Session,
    accountState: AccountState,
    profileState: ProfileState,
    monitoringState: MonitoringState,
    accountViewModel: AccountViewModel,
    profileViewModel: ProfileViewModel,
    monitoringViewModel: MonitoringViewModel,
    inventoryViewModel: InventoryViewModel = hiltViewModel(),
) {
    val observedInventoryState by inventoryViewModel.state.collectAsStateWithLifecycle()
    val inventoryState =
        if (observedInventoryState.userId == session.userId) observedInventoryState
        else InventoryState()
    LaunchedEffect(session.userId) { inventoryViewModel.loadForUser(session.userId) }
    val inventoryActions =
        remember(inventoryViewModel) {
            InventoryNavigationActions(
                inventoryViewModel::refresh,
                inventoryViewModel::clearMessage,
                inventoryViewModel::register,
                inventoryViewModel::updateStock,
            )
        }
    val navController = rememberNavController()
    val navigator = remember(navController) { AppNavigator(navController) }
    val entry by navController.currentBackStackEntryAsState()
    val destination = AppDestination.from(entry?.destination)
    val selectedTab =
        TopLevelDestination.entries.firstOrNull { it.contains(entry?.destination) }
            ?: TopLevelDestination.HOME
    val snackbarHostState = remember { SnackbarHostState() }
    val monitoringActions =
        remember(monitoringViewModel) { monitoringViewModel.navigationActions() }
    val profileActions =
        remember(profileViewModel, accountViewModel) {
            ProfileNavigationActions(
                profileViewModel::save,
                profileViewModel::refresh,
                accountViewModel::logout,
                profileViewModel::saveThresholds,
            )
        }
    var reauth by rememberSaveable { mutableStateOf(false) }
    var previousToken by remember { mutableStateOf(session.token) }
    if (reauth) {
        ReauthenticationRoute(
            accountState,
            previousToken,
            accountViewModel::login,
            accountViewModel::clearMessage,
            onLogout = accountViewModel::logout,
            onRequestVerification = accountViewModel::requestVerification,
            onRequestPasswordReset = accountViewModel::requestPasswordReset,
        ) {
            reauth = false
        }
        return
    }
    AppCreationEffect(
        monitoringState,
        navigator,
        snackbarHostState,
        monitoringViewModel::clearMessage,
    )
    SignedInContent(
        destination = destination,
        selectedTab = selectedTab,
        monitoringState = monitoringState,
        profileState = profileState,
        snackbarHostState = snackbarHostState,
        sessionExpired =
            session.expired() ||
                monitoringState.error == "UNAUTHENTICATED" ||
                profileState.error == "UNAUTHENTICATED" ||
                inventoryState.error == "UNAUTHENTICATED",
        onNavigate = { tab ->
            monitoringViewModel.clearMessage()
            navigator.topLevel(tab)
        },
        onBack = {
            navigator.back()
            monitoringViewModel.clearMessage()
        },
        onRefresh = {
            if (destination == AppDestination.INVENTORY) inventoryViewModel.refresh()
            else monitoringViewModel.refresh()
        },
        onSignIn = {
            previousToken = session.token
            reauth = true
        },
    ) {
        TerraTechNavHost(
            navController = navController,
            navigator = navigator,
            session = session,
            profileState = profileState,
            monitoringState = monitoringState,
            monitoringActions = monitoringActions,
            inventoryState = inventoryState,
            inventoryActions = inventoryActions,
            profileActions = profileActions,
        )
    }
}
