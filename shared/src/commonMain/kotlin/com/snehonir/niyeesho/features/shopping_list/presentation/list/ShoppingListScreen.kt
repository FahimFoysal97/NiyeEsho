package com.snehonir.niyeesho.features.shopping_list.presentation.list

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.snehonir.niyeesho.core.utils.ObserveAsEvents
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun ShoppingListScreen(
    onNavigateToDetail: (String) -> Unit,
    onNavigateToContacts: () -> Unit,
    onNavigateToProfile: () -> Unit,
    viewModel: ShoppingListViewModel = koinViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    ObserveAsEvents(viewModel.uiEvent) { event ->
        when (event) {
            is ShoppingListUiEvent.NavigateToDetail -> onNavigateToDetail(event.id)
            ShoppingListUiEvent.NavigateToContacts -> onNavigateToContacts()
            ShoppingListUiEvent.NavigateToProfile -> onNavigateToProfile()
            is ShoppingListUiEvent.ShowError -> Unit
        }
    }
    ShoppingListContent(
        uiState = uiState,
        onAction = viewModel::onAction,
        onNavigateToContacts = { viewModel.onAction(ShoppingListUiAction.OpenContacts) },
        onNavigateToProfile = { viewModel.onAction(ShoppingListUiAction.OpenProfile) },
    )
}