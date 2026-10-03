package com.snehonir.niyeesho.features.shopping_list.presentation.detail

sealed interface ListDetailUiEvent {
    data class ShowError(val message: String) : ListDetailUiEvent
    data class NavigateToDetail(val listId: String) : ListDetailUiEvent
    data object OpenInvite : ListDetailUiEvent
}
