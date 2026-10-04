package com.snehonir.niyeesho.features.auth.presentation.profile

sealed interface ProfileUiAction {
    data object Load : ProfileUiAction
    data object ManualSyncClicked : ProfileUiAction
    data object SignOutClicked : ProfileUiAction
    data object ErrorDismissed : ProfileUiAction
}
