package com.snehonir.niyeesho.features.shopping_list.presentation.detail

import com.snehonir.niyeesho.features.shopping_list.domain.entity.ShoppingList
import com.snehonir.niyeesho.features.shopping_list.presentation.detail.model.ListItemUiModel
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

data class ListDetailUiState(
    val list: ShoppingList? = null,
    val activeItems: ImmutableList<ListItemUiModel> = persistentListOf(),
    val checkedItems: ImmutableList<ListItemUiModel> = persistentListOf(),
    val isLoading: Boolean = true,
    val error: String? = null,
    val inviteLink: String? = null,
) {
    companion object {
        fun preview() = ListDetailUiState(
            list = ShoppingList.preview(),
            activeItems = persistentListOf(ListItemUiModel.fromDomain(com.snehonir.niyeesho.features.shopping_list.domain.entity.ListItem.preview())),
            isLoading = false,
        )
    }
}