package com.snehonir.niyeesho.features.shopping_list.presentation.list.model

import com.snehonir.niyeesho.core.utils.SyncStatus
import com.snehonir.niyeesho.features.shopping_list.domain.entity.ShoppingList

data class ShoppingListUiModel(
    val id: String,
    val title: String,
    val memberCount: Int,
    val syncStatus: SyncStatus,
) {
    companion object {
        fun fromDomain(list: ShoppingList) = ShoppingListUiModel(list.id, list.title, list.members.size, list.syncStatus)
    }
}
