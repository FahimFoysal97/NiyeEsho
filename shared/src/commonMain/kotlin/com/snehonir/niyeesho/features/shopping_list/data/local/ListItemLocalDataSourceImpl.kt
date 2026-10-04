package com.snehonir.niyeesho.features.shopping_list.data.local

import com.russhwolf.settings.Settings
import com.snehonir.niyeesho.features.shopping_list.domain.entity.ListItem
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.json.Json

class ListItemLocalDataSourceImpl(
    private val settings: Settings = Settings(),
    private val json: Json = Json { ignoreUnknownKeys = true },
) : ListItemLocalDataSource {
    private val itemsFlow = MutableStateFlow<List<ListItem>>(loadItems())

    private fun loadItems(): List<ListItem> {
        val jsonStr = settings.getString(key = KEY_ITEMS, defaultValue = "")
        if (jsonStr.isBlank()) return listOf(ListItem.preview())
        return try {
            json.decodeFromString(ListSerializer(ListItem.serializer()), jsonStr)
        } catch (_: Exception) {
            listOf(ListItem.preview())
        }
    }

    private fun persist(updated: List<ListItem>) {
        itemsFlow.value = updated
        settings.putString(key = KEY_ITEMS, value = json.encodeToString(ListSerializer(ListItem.serializer()), updated))
    }

    override fun watchForList(listId: String): Flow<List<ListItem>> =
        itemsFlow.map { all -> all.filter { it.listId == listId }.sortedBy { it.position } }

    override suspend fun get(itemId: String): ListItem? = itemsFlow.value.firstOrNull { it.id == itemId }

    override suspend fun upsert(item: ListItem) {
        val current = itemsFlow.value.filterNot { it.id == item.id } + item
        persist(current)
    }

    override suspend fun delete(itemId: String) {
        val current = itemsFlow.value.filterNot { it.id == itemId }
        persist(current)
    }

    override suspend fun reorder(listId: String, orderedIds: List<String>) {
        val current = itemsFlow.value.map { item ->
            if (item.listId == listId) {
                item.copy(position = orderedIds.indexOf(item.id).takeIf { it >= 0 } ?: item.position)
            } else item
        }
        persist(current)
    }

    companion object {
        private const val KEY_ITEMS = "list_items"
    }
}
