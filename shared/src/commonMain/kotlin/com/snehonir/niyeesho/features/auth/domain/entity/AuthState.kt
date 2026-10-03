package com.snehonir.niyeesho.features.auth.domain.entity

sealed interface AuthState {
    data object SignedOut : AuthState
    data object Loading : AuthState
    data class SignedIn(val user: User) : AuthState
    data class Error(val message: String) : AuthState
}
