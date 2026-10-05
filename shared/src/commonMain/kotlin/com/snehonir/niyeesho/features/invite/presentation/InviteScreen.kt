package com.snehonir.niyeesho.features.invite.presentation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.snehonir.niyeesho.core.utils.ObserveAsEvents
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun InviteScreen(
    onShareInvite: (String) -> Unit,
    onNavigateToLists: () -> Unit = {},
    onNavigateToContacts: () -> Unit = {},
    onNavigateToProfile: () -> Unit = {},
    viewModel: InviteViewModel = koinViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    ObserveAsEvents(viewModel.uiEvent) { event ->
        when (event) {
            is InviteUiEvent.ShareLink -> onShareInvite(event.link)
            is InviteUiEvent.ShowToast -> Unit
        }
    }

    InviteContent(
        uiState = uiState,
        onAction = viewModel::onAction,
        onNavigateToLists = onNavigateToLists,
        onNavigateToContacts = onNavigateToContacts,
        onNavigateToProfile = onNavigateToProfile,
    )
}
