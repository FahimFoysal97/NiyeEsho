package com.snehonir.niyeesho.features.auth.presentation.signin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.snehonir.niyeesho.features.auth.domain.usecase.SignInWithGoogleUseCase
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class SignInViewModel(
    private val signInWithGoogle: SignInWithGoogleUseCase,
) : ViewModel() {
    private val _uiState = MutableStateFlow(SignInUiState())
    val uiState = _uiState.asStateFlow()

    private val _uiEvent = Channel<SignInUiEvent>(Channel.BUFFERED)
    val uiEvent: Flow<SignInUiEvent> = _uiEvent.receiveAsFlow()

    fun onAction(action: SignInUiAction) {
        when (action) {
            is SignInUiAction.GoogleSignInClicked -> signIn(action.idToken)
            is SignInUiAction.GoogleSignInFailed -> {
                val message = action.throwable.message ?: "Unable to sign in with Google."
                _uiState.update { it.copy(isLoading = false, error = com.snehonir.niyeesho.core.utils.UiText.Plain(message)) }
                viewModelScope.launch { _uiEvent.send(SignInUiEvent.ShowError(message)) }
            }
            SignInUiAction.ErrorDismissed -> _uiState.update { it.copy(error = null) }
        }
    }

    private fun signIn(idToken: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            signInWithGoogle(idToken)
                .onSuccess { _uiEvent.send(SignInUiEvent.NavigateToShoppingLists) }
                .onFailure { throwable ->
                    val message = throwable.message ?: "Unable to sign in."
                    _uiState.update { it.copy(error = com.snehonir.niyeesho.core.utils.UiText.Plain(message)) }
                    _uiEvent.send(SignInUiEvent.ShowError(message))
                }
            _uiState.update { it.copy(isLoading = false) }
        }
    }
}
