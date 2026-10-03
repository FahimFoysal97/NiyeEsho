package com.snehonir.niyeesho.features.shopping_list.domain.repository

import com.snehonir.niyeesho.features.shopping_list.domain.entity.ShoppingList
import kotlinx.coroutines.flow.Flow

interface ShoppingListRepository {
    fun watchShoppingLists(): Flow<Result<List<ShoppingList>>>
    suspend fun getShoppingList(id: String): Result<ShoppingList>
    suspend fun createShoppingList(title: String, ownerId: String): Result<ShoppingList>
    suspend fun updateShoppingList(list: ShoppingList): Result<Unit>
    suspend fun deleteShoppingList(id: String): Result<Unit>
    suspend fun archiveShoppingList(id: String): Result<Unit>
    suspend fun inviteMember(listId: String, memberId: String): Result<Unit>
    suspend fun removeMember(listId: String, memberId: String): Result<Unit>
}
