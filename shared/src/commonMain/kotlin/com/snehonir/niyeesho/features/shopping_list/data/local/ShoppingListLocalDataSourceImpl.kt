package com.snehonir.niyeesho.features.shopping_list.data.local

import com.russhwolf.settings.Settings
import com.snehonir.niyeesho.features.shopping_list.domain.entity.ShoppingList
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.json.Json

class ShoppingListLocalDataSourceImpl(
    private val settings: Settings = Settings(),
    private val json: Json = Json { ignoreUnknownKeys = true },
) : ShoppingListLocalDataSource {
    private val listsFlow = MutableStateFlow<List<ShoppingList>>(loadLists())

    private fun loadLists(): List<ShoppingList> {
        val jsonStr = settings.getString(key = KEY_LISTS, defaultValue = "")
        if (jsonStr.isBlank()) return listOf(ShoppingList.preview())
        return try {
            json.decodeFromString(ListSerializer(ShoppingList.serializer()), jsonStr)
        } catch (_: Exception) {
            listOf(ShoppingList.preview())
        }
    }

    private fun persist(updated: List<ShoppingList>) {
        listsFlow.value = updated
        settings.putString(key = KEY_LISTS, value = json.encodeToString(ListSerializer(ShoppingList.serializer()), updated))
    }

    override fun watchAll(): Flow<List<ShoppingList>> = listsFlow.asStateFlow()
    override suspend fun get(id: String): ShoppingList? = listsFlow.value.firstOrNull { it.id == id }
    override suspend fun upsert(list: ShoppingList) {
        val current = listsFlow.value.filterNot { it.id == list.id } + list
        persist(current)
    }
    override suspend fun delete(id: String) {
        val current = listsFlow.value.filterNot { it.id == id }
        persist(current)
    }

    companion object {
        private const val KEY_LISTS = "shopping_lists"
    }
}
