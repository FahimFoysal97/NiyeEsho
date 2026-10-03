package com.snehonir.niyeesho.features.dashboard.presentation

sealed interface DashboardUiEvent {
    data class NavigateToDetail(val listId: String) : DashboardUiEvent
    data object NavigateToProfile : DashboardUiEvent
    data class ShareInvite(val link: String) : DashboardUiEvent
    data class ShowError(val message: String) : DashboardUiEvent
}
