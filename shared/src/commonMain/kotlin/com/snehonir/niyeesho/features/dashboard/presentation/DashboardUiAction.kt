package com.snehonir.niyeesho.features.dashboard.presentation

import com.snehonir.niyeesho.features.shopping_list.domain.entity.ListMember

sealed interface DashboardUiAction {
    data class ListClicked(val id: String) : DashboardUiAction
    data class DeleteList(val id: String) : DashboardUiAction
    data class RemoveContact(val uid: String) : DashboardUiAction
    data object FabClicked : DashboardUiAction
    data object FabMenuDismissed : DashboardUiAction
    data object AddGroupClicked : DashboardUiAction
    data object InviteShareClicked : DashboardUiAction
    data object InviteDismissed : DashboardUiAction
    data object CreateClicked : DashboardUiAction
    data object CreateDismissed : DashboardUiAction
    data class CreateConfirmed(
        val title: String,
        val ownerId: String,
        val members: List<ListMember> = emptyList(),
    ) : DashboardUiAction
    data object OpenProfile : DashboardUiAction
    data object SyncNowClicked : DashboardUiAction
    data object ErrorDismissed : DashboardUiAction
}
