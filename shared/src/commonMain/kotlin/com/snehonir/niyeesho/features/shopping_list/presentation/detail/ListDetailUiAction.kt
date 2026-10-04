package com.snehonir.niyeesho.features.shopping_list.presentation.detail

import com.snehonir.niyeesho.features.shopping_list.domain.entity.ListMember

sealed interface ListDetailUiAction {
    data class ToggleItem(val id: String, val checked: Boolean) : ListDetailUiAction
    data class DeleteItem(val id: String) : ListDetailUiAction
    data class AddItem(val name: String, val quantity: String?, val unit: String?) : ListDetailUiAction
    data object DeleteList : ListDetailUiAction
    data class UpdateList(val title: String) : ListDetailUiAction
    data class RemoveMember(val memberId: String) : ListDetailUiAction
    data object InviteMember : ListDetailUiAction
    data object DismissInvite : ListDetailUiAction
    data class AddMembersConfirmed(val members: List<ListMember>) : ListDetailUiAction
    data object CreateGroupClicked : ListDetailUiAction
    data object DismissCreateGroup : ListDetailUiAction
    data class CreateGroupConfirmed(val title: String, val members: List<ListMember>) : ListDetailUiAction
    data object OpenGroupSettings : ListDetailUiAction
    data object DismissGroupSettings : ListDetailUiAction
    data object OpenContactSettings : ListDetailUiAction
    data object DismissContactSettings : ListDetailUiAction
    data class AddContactFromMember(val member: ListMember) : ListDetailUiAction
    data object ErrorDismissed : ListDetailUiAction
}
