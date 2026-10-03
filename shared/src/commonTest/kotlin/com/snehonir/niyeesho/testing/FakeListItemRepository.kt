package com.snehonir.niyeesho.testing

import com.snehonir.niyeesho.features.shopping_list.domain.entity.ListItem
import com.snehonir.niyeesho.features.shopping_list.domain.repository.ListItemRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map

class FakeListItemRepository : ListItemRepository {
    private val items = MutableStateFlow(listOf(ListItem.preview()))
    override fun watchListItems(listId: String): Flow<Result<List<ListItem>>> =
        items.map { all -> Result.success(all.filter { it.listId == listId }) }
    override suspend fun addItem(item: ListItem): Result<Unit> {
        items.value += item
        return Result.success(Unit)
    }
    override suspend fun updateItem(item: ListItem): Result<Unit> = Result.success(Unit)
    override suspend fun toggleItem(itemId: String, checked: Boolean, checkedBy: String?): Result<Unit> =
        Result.success(Unit)
    override suspend fun deleteItem(itemId: String): Result<Unit> = Result.success(Unit)
    override suspend fun reorderItems(listId: String, orderedIds: List<String>): Result<Unit> =
        Result.success(Unit)
}
