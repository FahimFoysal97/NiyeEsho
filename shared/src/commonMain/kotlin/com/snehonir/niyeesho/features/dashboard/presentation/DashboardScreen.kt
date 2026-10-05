package com.snehonir.niyeesho.features.dashboard.presentation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.snehonir.niyeesho.core.utils.ObserveAsEvents
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun DashboardScreen(
    onNavigateToDetail: (String) -> Unit,
    onNavigateToContacts: () -> Unit = {},
    onNavigateToProfile: () -> Unit = {},
    onNavigateToInvite: () -> Unit = {},
    onShareInvite: (String) -> Unit = {},
    modifier: Modifier = Modifier,
    viewModel: DashboardViewModel = koinViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()

    ObserveAsEvents(viewModel.uiEvent) { event ->
        when (event) {
            is DashboardUiEvent.NavigateToDetail -> onNavigateToDetail(event.listId)
            DashboardUiEvent.NavigateToProfile -> onNavigateToProfile()
            is DashboardUiEvent.ShareInvite -> onShareInvite(event.link)
            is DashboardUiEvent.ShowError -> Unit
        }
    }

    DashboardContent(
        uiState = uiState,
        onAction = viewModel::onAction,
        onNavigateToDetail = onNavigateToDetail,
        onNavigateToContacts = onNavigateToContacts,
        onNavigateToProfile = onNavigateToProfile,
        onNavigateToInvite = onNavigateToInvite,
        onShareInvite = onShareInvite,
        modifier = modifier,
    )
}
