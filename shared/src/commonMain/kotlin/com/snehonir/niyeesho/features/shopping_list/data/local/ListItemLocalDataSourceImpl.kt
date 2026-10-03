package com.snehonir.niyeesho.features.shopping_list.data.local

import com.snehonir.niyeesho.features.shopping_list.domain.entity.ListItem
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map

class ListItemLocalDataSourceImpl : ListItemLocalDataSource {
    private val items = MutableStateFlow(listOf(ListItem.preview()))
    override fun watchForList(listId: String): Flow<List<ListItem>> =
        items.map { all -> all.filter { it.listId == listId }.sortedBy { it.position } }
    override suspend fun get(itemId: String): ListItem? = items.value.firstOrNull { it.id == itemId }
    override suspend fun upsert(item: ListItem) {
        items.value = items.value.filterNot { it.id == item.id } + item
    }
    override suspend fun delete(itemId: String) {
        items.value = items.value.filterNot { it.id == itemId }
    }
    override suspend fun reorder(listId: String, orderedIds: List<String>) {
        items.value = items.value.map { item ->
            if (item.listId == listId) {
                item.copy(position = orderedIds.indexOf(item.id).takeIf { it >= 0 } ?: item.position)
            } else item
        }
    }
}
