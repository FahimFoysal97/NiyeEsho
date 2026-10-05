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
    onNavigateToLists: () -> Unit = {},
    onNavigateToProfile: () -> Unit = {},
    onNavigateToInvite: () -> Unit = {},
    viewModel: ContactsViewModel = koinViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    ObserveAsEvents(viewModel.uiEvent) { event ->
        when (event) {
            is ContactsUiEvent.OpenContact -> onOpenContact(event.uid)
            is ContactsUiEvent.GroupCreated -> onOpenContact(event.listId)
            is ContactsUiEvent.ShareInvite -> onShareInvite(event.link)
            is ContactsUiEvent.ShowError -> Unit
        }
    }
    ContactsContent(
        uiState = uiState,
        onAction = viewModel::onAction,
        onBack = onBack,
        onNavigateToLists = onNavigateToLists,
        onNavigateToProfile = onNavigateToProfile,
        onNavigateToInvite = onNavigateToInvite,
    )
    if (uiState.isInviteBottomSheetVisible) {
        uiState.inviteLink?.let { link ->
            InviteBottomSheet(
                inviteLink = link,
                onShare = { onShareInvite(link) },
                onDismiss = { viewModel.onAction(ContactsUiAction.DismissInvite) },
            )
        }
    }
}
