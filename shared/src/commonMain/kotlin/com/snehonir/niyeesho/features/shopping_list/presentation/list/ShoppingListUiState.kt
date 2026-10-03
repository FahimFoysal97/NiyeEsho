package com.snehonir.niyeesho.features.shopping_list.presentation.list

import com.snehonir.niyeesho.features.shopping_list.presentation.list.model.ShoppingListUiModel
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

data class ShoppingListUiState(
    val lists: ImmutableList<ShoppingListUiModel> = persistentListOf(),
    val isLoading: Boolean = true,
    val isCreateDialogVisible: Boolean = false,
    val error: String? = null,
) {
    companion object {
        fun preview() = ShoppingListUiState(
            lists = persistentListOf(ShoppingListUiModel.fromDomain(com.snehonir.niyeesho.features.shopping_list.domain.entity.ShoppingList.preview())),
            isLoading = false,
        )
    }
}
