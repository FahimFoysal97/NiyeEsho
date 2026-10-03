package com.snehonir.niyeesho.features.auth.presentation.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.snehonir.niyeesho.features.auth.domain.usecase.GetCurrentUserUseCase
import com.snehonir.niyeesho.features.auth.domain.usecase.SignOutUseCase
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ProfileViewModel(
    private val getCurrentUser: GetCurrentUserUseCase,
    private val signOutUseCase: SignOutUseCase,
) : ViewModel() {
    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState = _uiState.asStateFlow()

    private val _uiEvent = Channel<ProfileUiEvent>(Channel.BUFFERED)
    val uiEvent: Flow<ProfileUiEvent> = _uiEvent.receiveAsFlow()

    init {
        onAction(ProfileUiAction.Load)
    }

    fun onAction(action: ProfileUiAction) {
        when (action) {
            ProfileUiAction.Load -> load()
            ProfileUiAction.SignOutClicked -> signOut()
            ProfileUiAction.ErrorDismissed -> _uiState.update { it.copy(error = null) }
        }
    }

    private fun load() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            getCurrentUser()
                .onSuccess { user -> _uiState.update { it.copy(user = user, isLoading = false) } }
                .onFailure { throwable -> fail(throwable) }
        }
    }

    private fun signOut() {
        viewModelScope.launch {
            _uiState.update { it.copy(isSigningOut = true) }
            signOutUseCase()
                .onSuccess { _uiEvent.send(ProfileUiEvent.NavigateToSignIn) }
                .onFailure { throwable -> fail(throwable) }
            _uiState.update { it.copy(isSigningOut = false) }
        }
    }

    private suspend fun fail(throwable: Throwable) {
        val message = throwable.message ?: "Unable to load profile."
        _uiState.update { it.copy(isLoading = false, error = message) }
        _uiEvent.send(ProfileUiEvent.ShowError(message))
    }
}
