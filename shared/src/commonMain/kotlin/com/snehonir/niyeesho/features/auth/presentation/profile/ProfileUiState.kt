package com.snehonir.niyeesho.features.auth.presentation.profile

import com.snehonir.niyeesho.features.auth.domain.entity.User

data class ProfileUiState(
    val user: User? = null,
    val isLoading: Boolean = true,
    val isSigningOut: Boolean = false,
    val error: String? = null,
) {
    companion object {
        fun preview() = ProfileUiState(user = User.preview(), isLoading = false)
    }
}
