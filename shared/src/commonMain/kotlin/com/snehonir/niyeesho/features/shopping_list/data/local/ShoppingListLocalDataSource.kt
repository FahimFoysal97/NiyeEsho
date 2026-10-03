package com.snehonir.niyeesho.features.shopping_list.data.local

import com.snehonir.niyeesho.features.shopping_list.domain.entity.ShoppingList
import kotlinx.coroutines.flow.Flow

interface ShoppingListLocalDataSource {
    fun watchAll(): Flow<List<ShoppingList>>
    suspend fun get(id: String): ShoppingList?
    suspend fun upsert(list: ShoppingList)
    suspend fun delete(id: String)
}
