package com.snehonir.niyeesho.features.contacts.presentation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.snehonir.niyeesho.core.utils.ObserveAsEvents
import com.snehonir.niyeesho.features.contacts.presentation.component.InviteBottomSheet
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun ContactsScreen(
    onOpenContact: (String) -> Unit,
    onShareInvite: (String) -> Unit,
    onBack: () -> Unit,
    viewModel: ContactsViewModel = koinViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    ObserveAsEvents(viewModel.uiEvent) { event ->
        when (event) {
            is ContactsUiEvent.OpenContact -> onOpenContact(event.uid)
            is ContactsUiEvent.ShareInvite -> onShareInvite(event.link)
            is ContactsUiEvent.ShowError -> Unit
        }
    }
    ContactsContent(uiState = uiState, onAction = viewModel::onAction, onBack = onBack)
    uiState.inviteLink?.let { link ->
        InviteBottomSheet(
            inviteLink = link,
            onShare = { onShareInvite(link) },
            onDismiss = { viewModel.onAction(ContactsUiAction.DismissInvite) },
        )
    }
}
