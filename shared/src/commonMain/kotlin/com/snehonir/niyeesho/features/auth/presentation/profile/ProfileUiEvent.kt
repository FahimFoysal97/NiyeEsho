package com.snehonir.niyeesho.features.auth.presentation.profile

sealed interface ProfileUiEvent {
    data object NavigateToSignIn : ProfileUiEvent
    data class ShowError(val message: String) : ProfileUiEvent
}
