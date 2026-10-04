package com.snehonir.niyeesho.features.auth.presentation.profile

import com.snehonir.niyeesho.features.auth.domain.entity.User
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant

data class ProfileUiState(
    val user: User? = null,
    val listCount: Int = 0,
    val contactCount: Int = 0,
    val isLoading: Boolean = true,
    val isSigningOut: Boolean = false,
    val isSyncing: Boolean = false,
    val isSyncedSuccessfully: Boolean = false,
    val lastSyncedAt: Instant? = null,
    val error: String? = null,
) {
    companion object {
        fun preview() = ProfileUiState(user = User.preview(), listCount = 3, contactCount = 3, isLoading = false, lastSyncedAt = Clock.System.now())
    }
}
