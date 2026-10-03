package com.snehonir.niyeesho.features.shopping_list.presentation.detail

sealed interface ListDetailUiAction {
    data class ToggleItem(val id: String, val checked: Boolean) : ListDetailUiAction
    data class DeleteItem(val id: String) : ListDetailUiAction
    data class AddItem(val name: String, val quantity: String?, val unit: String?) : ListDetailUiAction
    data object InviteMember : ListDetailUiAction
    data object DismissInvite : ListDetailUiAction
    data object ErrorDismissed : ListDetailUiAction
}