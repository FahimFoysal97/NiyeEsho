package com.snehonir.niyeesho.features.shopping_list.presentation.detail

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.snehonir.niyeesho.core.utils.ObserveAsEvents
import com.snehonir.niyeesho.features.shopping_list.presentation.detail.component.InviteMemberSheet
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun ListDetailScreen(
    listId: String,
    onInviteMember: () -> Unit = {},
    onShareInvite: (String) -> Unit = {},
    viewModel: ListDetailViewModel = koinViewModel(),
) {
    LaunchedEffect(listId) { viewModel.load(listId) }
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    ObserveAsEvents(viewModel.uiEvent) { event ->
        when (event) {
            ListDetailUiEvent.OpenInvite -> onInviteMember()
            is ListDetailUiEvent.ShowError -> Unit
        }
    }
    ListDetailContent(uiState, viewModel::onAction)
    uiState.inviteLink?.let { inviteLink ->
        InviteMemberSheet(
            inviteLink = inviteLink,
            onShare = { onShareInvite(inviteLink) },
            onDismiss = { viewModel.onAction(ListDetailUiAction.DismissInvite) },
        )
    }
}