package com.snehonir.niyeesho.features.auth.presentation.profile

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.snehonir.niyeesho.core.utils.ObserveAsEvents
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun ProfileScreen(
    onNavigateToSignIn: () -> Unit,
    onBack: () -> Unit,
    onNavigateToLists: () -> Unit = {},
    onNavigateToContacts: () -> Unit = {},
    viewModel: ProfileViewModel = koinViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    ObserveAsEvents(viewModel.uiEvent) { event ->
        when (event) {
            ProfileUiEvent.NavigateToSignIn -> onNavigateToSignIn()
            is ProfileUiEvent.ShowError -> Unit
        }
    }
    ProfileContent(
        uiState = uiState,
        onAction = viewModel::onAction,
        onBack = onBack,
        onNavigateToLists = onNavigateToLists,
        onNavigateToContacts = onNavigateToContacts,
    )
}
