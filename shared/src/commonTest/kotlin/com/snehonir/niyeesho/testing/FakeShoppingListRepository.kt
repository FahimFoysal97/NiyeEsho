package com.snehonir.niyeesho.testing

import com.snehonir.niyeesho.features.shopping_list.domain.entity.ShoppingList
import com.snehonir.niyeesho.features.shopping_list.domain.repository.ShoppingListRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map

class FakeShoppingListRepository : ShoppingListRepository {
    private val lists = MutableStateFlow(listOf(ShoppingList.preview()))

    override fun watchShoppingLists(): Flow<Result<List<ShoppingList>>> =
        lists.asStateFlow().map { Result.success(it) }
    override suspend fun getShoppingList(id: String): Result<ShoppingList> =
        lists.value.firstOrNull { it.id == id }?.let(Result.Companion::success)
            ?: Result.failure(NoSuchElementException())
    override suspend fun createShoppingList(title: String, ownerId: String): Result<ShoppingList> =
        Result.success(ShoppingList.preview().copy(title = title, ownerId = ownerId))
    override suspend fun updateShoppingList(list: ShoppingList): Result<Unit> = Result.success(Unit)
    override suspend fun deleteShoppingList(id: String): Result<Unit> = Result.success(Unit)
    override suspend fun archiveShoppingList(id: String): Result<Unit> = Result.success(Unit)
    override suspend fun inviteMember(listId: String, memberId: String): Result<Unit> = Result.success(Unit)
    override suspend fun removeMember(listId: String, memberId: String): Result<Unit> = Result.success(Unit)
}
