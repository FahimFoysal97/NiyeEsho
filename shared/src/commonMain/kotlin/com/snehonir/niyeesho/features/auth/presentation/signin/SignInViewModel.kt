package com.snehonir.niyeesho.features.auth.presentation.signin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.snehonir.niyeesho.core.utils.logD
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

    init {
        logD("SignInViewModel", "SignInViewModel initialized")
    }

    fun onAction(action: SignInUiAction) {
        logD("SignInViewModel", "onAction called with action: $action")
        when (action) {
            is SignInUiAction.GoogleSignInClicked -> signIn(action.idToken)
            is SignInUiAction.GoogleSignInFailed -> {
                val message = action.throwable.message ?: "Unable to sign in with Google."
                logD("SignInViewModel", "GoogleSignInFailed error message: $message")
                _uiState.update { it.copy(isLoading = false, error = com.snehonir.niyeesho.core.utils.UiText.Plain(message)) }
                viewModelScope.launch { _uiEvent.send(SignInUiEvent.ShowError(message)) }
            }
            SignInUiAction.ErrorDismissed -> {
                logD("SignInViewModel", "ErrorDismissed action handled")
                _uiState.update { it.copy(error = null) }
            }
        }
    }

    private fun signIn(idToken: String) {
        logD("SignInViewModel", "signIn() started with idToken length: ${idToken.length}")
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            signInWithGoogle(idToken)
                .onSuccess {
                    logD("SignInViewModel", "signInWithGoogle succeeded, sending NavigateToShoppingLists event")
                    _uiEvent.send(SignInUiEvent.NavigateToShoppingLists)
                }
                .onFailure { throwable ->
                    val message = throwable.message ?: "Unable to sign in."
                    logD("SignInViewModel", "signInWithGoogle failed with error: $message")
                    _uiState.update { it.copy(error = com.snehonir.niyeesho.core.utils.UiText.Plain(message)) }
                    _uiEvent.send(SignInUiEvent.ShowError(message))
                }
            _uiState.update { it.copy(isLoading = false) }
            logD("SignInViewModel", "signIn() completed")
        }
    }
}
