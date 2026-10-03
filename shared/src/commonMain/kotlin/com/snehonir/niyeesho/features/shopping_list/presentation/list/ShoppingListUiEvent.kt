package com.snehonir.niyeesho.features.shopping_list.presentation.list

sealed interface ShoppingListUiEvent {
    data class NavigateToDetail(val id: String) : ShoppingListUiEvent
    data object NavigateToContacts : ShoppingListUiEvent
    data object NavigateToProfile : ShoppingListUiEvent
    data class ShowError(val message: String) : ShoppingListUiEvent
}