package com.snehonir.niyeesho.features.auth.presentation.signin

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.snehonir.niyeesho.core.utils.ObserveAsEvents
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun SignInScreen(
    onNavigateToShoppingLists: () -> Unit,
    viewModel: SignInViewModel = koinViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    ObserveAsEvents(viewModel.uiEvent) { event ->
        when (event) {
            SignInUiEvent.NavigateToShoppingLists -> onNavigateToShoppingLists()
            is SignInUiEvent.ShowError -> Unit
        }
    }
    SignInContent(uiState = uiState, onAction = viewModel::onAction)
}
