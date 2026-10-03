package com.snehonir.niyeesho.features.auth.presentation.signin

import com.snehonir.niyeesho.core.utils.UiText

data class SignInUiState(
    val isLoading: Boolean = false,
    val error: UiText? = null,
) {
    companion object {
        fun preview() = SignInUiState()
    }
}
