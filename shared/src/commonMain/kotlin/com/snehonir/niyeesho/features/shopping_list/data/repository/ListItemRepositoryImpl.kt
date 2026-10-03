package com.snehonir.niyeesho.features.shopping_list.data.repository

import com.snehonir.niyeesho.features.shopping_list.data.local.ListItemLocalDataSource
import com.snehonir.niyeesho.features.shopping_list.domain.entity.ListItem
import com.snehonir.niyeesho.features.shopping_list.domain.repository.ListItemRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.datetime.Clock

class ListItemRepositoryImpl(
    private val local: ListItemLocalDataSource,
) : ListItemRepository {
    override fun watchListItems(listId: String): Flow<Result<List<ListItem>>> =
        local.watchForList(listId).map { Result.success(it) }.catch { emit(Result.failure(it)) }
    override suspend fun addItem(item: ListItem): Result<Unit> = runCatching { local.upsert(item) }
    override suspend fun updateItem(item: ListItem): Result<Unit> = runCatching {
        local.upsert(item.copy(updatedAt = Clock.System.now()))
    }
    override suspend fun toggleItem(itemId: String, checked: Boolean, checkedBy: String?): Result<Unit> =
        runCatching { local.get(itemId)?.let { local.upsert(it.copy(isChecked = checked, checkedBy = checkedBy)) } }
    override suspend fun deleteItem(itemId: String): Result<Unit> = runCatching { local.delete(itemId) }
    override suspend fun reorderItems(listId: String, orderedIds: List<String>): Result<Unit> =
        runCatching { local.reorder(listId, orderedIds) }
}
