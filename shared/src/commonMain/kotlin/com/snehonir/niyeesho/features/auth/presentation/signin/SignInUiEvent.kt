package com.snehonir.niyeesho.features.auth.presentation.signin

sealed interface SignInUiEvent {
    data object NavigateToShoppingLists : SignInUiEvent
    data class ShowError(val message: String) : SignInUiEvent
}
