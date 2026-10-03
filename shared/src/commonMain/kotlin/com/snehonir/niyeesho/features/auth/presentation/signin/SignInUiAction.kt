package com.snehonir.niyeesho.features.auth.presentation.signin

sealed interface SignInUiAction {
    data class GoogleSignInClicked(val idToken: String) : SignInUiAction
    data class GoogleSignInFailed(val throwable: Throwable) : SignInUiAction
    data object ErrorDismissed : SignInUiAction
}
