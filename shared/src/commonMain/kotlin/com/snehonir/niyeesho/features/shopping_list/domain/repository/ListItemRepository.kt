package com.snehonir.niyeesho.features.shopping_list.domain.repository

import com.snehonir.niyeesho.features.shopping_list.domain.entity.ListItem
import kotlinx.coroutines.flow.Flow

interface ListItemRepository {
    fun watchListItems(listId: String): Flow<Result<List<ListItem>>>
    suspend fun addItem(item: ListItem): Result<Unit>
    suspend fun updateItem(item: ListItem): Result<Unit>
    suspend fun toggleItem(itemId: String, checked: Boolean, checkedBy: String?): Result<Unit>
    suspend fun deleteItem(itemId: String): Result<Unit>
    suspend fun reorderItems(listId: String, orderedIds: List<String>): Result<Unit>
}
