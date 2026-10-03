package com.snehonir.niyeesho.features.shopping_list.data.local

import com.snehonir.niyeesho.features.shopping_list.domain.entity.ShoppingList
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class ShoppingListLocalDataSourceImpl : ShoppingListLocalDataSource {
    private val lists = MutableStateFlow(listOf(ShoppingList.preview()))
    override fun watchAll(): Flow<List<ShoppingList>> = lists.asStateFlow()
    override suspend fun get(id: String): ShoppingList? = lists.value.firstOrNull { it.id == id }
    override suspend fun upsert(list: ShoppingList) {
        lists.value = lists.value.filterNot { it.id == list.id } + list
    }
    override suspend fun delete(id: String) {
        lists.value = lists.value.filterNot { it.id == id }
    }
}
