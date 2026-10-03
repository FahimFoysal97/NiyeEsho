package com.snehonir.niyeesho.features.dashboard.presentation

sealed interface DashboardUiAction {
    data class ListClicked(val id: String) : DashboardUiAction
    data object CreateClicked : DashboardUiAction
    data object CreateDismissed : DashboardUiAction
    data class CreateConfirmed(val title: String, val ownerId: String) : DashboardUiAction
    data object OpenProfile : DashboardUiAction
    data object SyncNowClicked : DashboardUiAction
    data object ErrorDismissed : DashboardUiAction
}
