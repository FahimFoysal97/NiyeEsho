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
    onNavigateToProfile: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: DashboardViewModel = koinViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()

    ObserveAsEvents(viewModel.uiEvent) { event ->
        when (event) {
            is DashboardUiEvent.NavigateToDetail -> onNavigateToDetail(event.listId)
            /*is DashboardUiEvent.NavigateToContactList -> {
                // Handle contact tap if needed
            }*/
            DashboardUiEvent.NavigateToProfile -> onNavigateToProfile()
            is DashboardUiEvent.ShowError -> Unit
        }
    }

    DashboardContent(
        uiState = uiState,
        onAction = viewModel::onAction,
        onNavigateToDetail = onNavigateToDetail,
        onNavigateToProfile = onNavigateToProfile,
        modifier = modifier,
    )
}
