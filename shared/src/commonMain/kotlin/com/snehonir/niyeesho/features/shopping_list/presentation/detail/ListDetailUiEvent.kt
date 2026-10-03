package com.snehonir.niyeesho.features.shopping_list.presentation.detail

sealed interface ListDetailUiEvent {
    data class ShowError(val message: String) : ListDetailUiEvent
    data object OpenInvite : ListDetailUiEvent
}
