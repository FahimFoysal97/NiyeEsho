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
    override suspend fun createShoppingList(
        title: String,
        ownerId: String,
        members: List<com.snehonir.niyeesho.features.shopping_list.domain.entity.ListMember>,
        id: String?,
    ): Result<ShoppingList> {
        val currentUserMember = com.snehonir.niyeesho.features.shopping_list.domain.entity.ListMember(ownerId, ownerId, null)
        val allMembers = (listOf(currentUserMember) + members).distinctBy { it.uid }
        val listId = id ?: "list_${lists.value.size + 1}"
        val list = ShoppingList.preview().copy(
            id = listId,
            title = title,
            ownerId = ownerId,
            memberIds = allMembers.map { it.uid },
            members = allMembers,
        )
        lists.value = lists.value + list
        return Result.success(list)
    }
    override suspend fun updateShoppingList(list: ShoppingList): Result<Unit> = Result.success(Unit)
    override suspend fun deleteShoppingList(id: String): Result<Unit> = Result.success(Unit)
    override suspend fun archiveShoppingList(id: String): Result<Unit> = Result.success(Unit)
    override suspend fun inviteMember(listId: String, memberId: String): Result<Unit> = Result.success(Unit)
    override suspend fun addMembers(
        listId: String,
        members: List<com.snehonir.niyeesho.features.shopping_list.domain.entity.ListMember>,
    ): Result<Unit> {
        lists.value.firstOrNull { it.id == listId }?.let { list ->
            val updatedMembers = (list.members + members).distinctBy { it.uid }
            val updated = list.copy(
                members = updatedMembers,
                memberIds = updatedMembers.map { it.uid },
            )
            lists.value = lists.value.filterNot { it.id == listId } + updated
        }
        return Result.success(Unit)
    }
    override suspend fun removeMember(listId: String, memberId: String): Result<Unit> = Result.success(Unit)
}
