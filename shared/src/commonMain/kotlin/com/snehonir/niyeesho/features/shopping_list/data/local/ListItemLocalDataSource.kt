package com.snehonir.niyeesho.features.shopping_list.data.local

import com.snehonir.niyeesho.features.shopping_list.domain.entity.ListItem
import kotlinx.coroutines.flow.Flow

interface ListItemLocalDataSource {
    fun watchForList(listId: String): Flow<List<ListItem>>
    suspend fun get(itemId: String): ListItem?
    suspend fun upsert(item: ListItem)
    suspend fun delete(itemId: String)
    suspend fun reorder(listId: String, orderedIds: List<String>)
}
