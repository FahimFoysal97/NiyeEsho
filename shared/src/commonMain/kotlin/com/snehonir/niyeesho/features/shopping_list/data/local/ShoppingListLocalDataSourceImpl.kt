package com.snehonir.niyeesho.features.shopping_list.data.local

import com.russhwolf.settings.Settings
import com.snehonir.niyeesho.core.utils.logD
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
        logD("ShoppingListLocal(DB)", "loadLists() called")
        val jsonStr = settings.getString(key = KEY_LISTS, defaultValue = "")
        if (jsonStr.isBlank()) return listOf(ShoppingList.preview())
        return try {
            val lists = json.decodeFromString(ListSerializer(ShoppingList.serializer()), jsonStr)
            logD("ShoppingListLocal(DB)", "loadLists() -> loaded ${lists.size} lists from local settings")
            lists
        } catch (e: Exception) {
            logD("ShoppingListLocal(DB)", "loadLists() -> failed to decode: ${e.message}")
            listOf(ShoppingList.preview())
        }
    }

    private fun persist(updated: List<ShoppingList>) {
        logD("ShoppingListLocal(DB)", "persist() -> saving ${updated.size} lists to local settings")
        listsFlow.value = updated
        settings.putString(key = KEY_LISTS, value = json.encodeToString(ListSerializer(ShoppingList.serializer()), updated))
        logD("ShoppingListLocal(DB)", "persist() -> successfully saved to local settings")
    }

    override fun watchAll(): Flow<List<ShoppingList>> = listsFlow.asStateFlow().also {
        logD("ShoppingListLocal(DB)", "watchAll() called")
    }
    override suspend fun get(id: String): ShoppingList? = listsFlow.value.firstOrNull { it.id == id }.also {
        logD("ShoppingListLocal(DB)", "get() list id: $id -> found: ${it != null}")
    }
    override suspend fun upsert(list: ShoppingList) {
        logD("ShoppingListLocal(DB)", "upsert() list '${list.title}' (id: ${list.id})")
        val current = listsFlow.value.filterNot { it.id == list.id } + list
        persist(current)
    }
    override suspend fun delete(id: String) {
        logD("ShoppingListLocal(DB)", "delete() list id: $id")
        val current = listsFlow.value.filterNot { it.id == id }
        persist(current)
    }

    companion object {
        private const val KEY_LISTS = "shopping_lists"
    }
}
