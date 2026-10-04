package com.snehonir.niyeesho.features.shopping_list.data.repository

import com.snehonir.niyeesho.core.utils.logD
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

    override suspend fun addItem(item: ListItem): Result<Unit> = runCatching {
        logD("ListItemRepo", "addItem() -> inserting item '${item.name}' (id: ${item.id}) for listId: ${item.listId} in LocalDB")
        local.upsert(item)
        logD("ListItemRepo", "addItem() -> successfully inserted item '${item.name}' in LocalDB")
    }.onFailure {
        logD("ListItemRepo", "addItem() -> failed to insert item: ${it.message}")
    }

    override suspend fun updateItem(item: ListItem): Result<Unit> = runCatching {
        logD("ListItemRepo", "updateItem() -> updating item '${item.name}' id: ${item.id} in LocalDB")
        local.upsert(item.copy(updatedAt = Clock.System.now()))
        logD("ListItemRepo", "updateItem() -> success")
    }.onFailure {
        logD("ListItemRepo", "updateItem() -> failed: ${it.message}")
    }

    override suspend fun toggleItem(itemId: String, checked: Boolean, checkedBy: String?): Result<Unit> =
        runCatching {
            logD("ListItemRepo", "toggleItem() -> toggling itemId: $itemId to checked: $checked")
            local.get(itemId)?.let {
                local.upsert(it.copy(isChecked = checked, checkedBy = checkedBy))
                logD("ListItemRepo", "toggleItem() -> success in LocalDB")
            } ?: logD("ListItemRepo", "toggleItem() -> item not found: $itemId")
        }.onFailure {
            logD("ListItemRepo", "toggleItem() -> failed: ${it.message}")
        }

    override suspend fun deleteItem(itemId: String): Result<Unit> = runCatching {
        logD("ListItemRepo", "deleteItem() -> deleting itemId: $itemId from LocalDB")
        local.delete(itemId)
        logD("ListItemRepo", "deleteItem() -> success")
    }.onFailure {
        logD("ListItemRepo", "deleteItem() -> failed: ${it.message}")
    }

    override suspend fun reorderItems(listId: String, orderedIds: List<String>): Result<Unit> =
        runCatching {
            logD("ListItemRepo", "reorderItems() for listId: $listId")
            local.reorder(listId, orderedIds)
            logD("ListItemRepo", "reorderItems() -> success")
        }.onFailure {
            logD("ListItemRepo", "reorderItems() -> failed: ${it.message}")
        }
}
