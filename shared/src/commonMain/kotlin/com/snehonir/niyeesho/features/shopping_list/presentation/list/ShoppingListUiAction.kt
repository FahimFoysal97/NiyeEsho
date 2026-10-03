package com.snehonir.niyeesho.features.shopping_list.presentation.list

sealed interface ShoppingListUiAction {
    data object CreateClicked : ShoppingListUiAction
    data class CreateConfirmed(val title: String, val ownerId: String) : ShoppingListUiAction
    data object CreateDismissed : ShoppingListUiAction
    data class ListClicked(val id: String) : ShoppingListUiAction
    data class DeleteClicked(val id: String) : ShoppingListUiAction
    data class ArchiveClicked(val id: String) : ShoppingListUiAction
    data object ErrorDismissed : ShoppingListUiAction
}
