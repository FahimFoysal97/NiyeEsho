package com.snehonir.niyeesho.features.shopping_list.data.repository

import com.snehonir.niyeesho.features.shopping_list.data.local.ShoppingListLocalDataSource
import com.snehonir.niyeesho.features.shopping_list.domain.entity.ListMember
import com.snehonir.niyeesho.features.shopping_list.domain.entity.ShoppingList
import com.snehonir.niyeesho.features.shopping_list.domain.repository.ShoppingListRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.datetime.Clock

class ShoppingListRepositoryImpl(
    private val local: ShoppingListLocalDataSource,
) : ShoppingListRepository {
    override fun watchShoppingLists(): Flow<Result<List<ShoppingList>>> =
        local.watchAll().map { Result.success(it.filterNot(ShoppingList::isArchived)) }
            .catch { emit(Result.failure(it)) }

    override suspend fun getShoppingList(id: String): Result<ShoppingList> =
        local.get(id)?.let { Result.success(it) }
            ?: Result.failure(NoSuchElementException("Shopping list not found."))

    override suspend fun createShoppingList(
        title: String,
        ownerId: String,
        members: List<ListMember>,
        id: String?,
    ): Result<ShoppingList> = runCatching {
        val now = Clock.System.now()
        val ownerMember = ListMember(ownerId, "Sneho Nir", null)
        val allMembers = (listOf(ownerMember) + members).distinctBy { it.uid }
        val allMemberIds = allMembers.map { it.uid }
        val listId = id ?: "list_${now.toEpochMilliseconds()}"
        val list = ShoppingList(
            id = listId,
            title = title,
            ownerId = ownerId,
            memberIds = allMemberIds,
            members = allMembers,
            createdAt = now,
            updatedAt = now,
            lastSyncedAt = null,
        )
        local.upsert(list)
        list
    }

    override suspend fun updateShoppingList(list: ShoppingList): Result<Unit> = runCatching {
        local.upsert(list.copy(updatedAt = Clock.System.now()))
    }
    override suspend fun deleteShoppingList(id: String): Result<Unit> = runCatching { local.delete(id) }
    override suspend fun archiveShoppingList(id: String): Result<Unit> = runCatching {
        local.get(id)?.let { local.upsert(it.copy(isArchived = true, updatedAt = Clock.System.now())) }
    }
    override suspend fun inviteMember(listId: String, memberId: String): Result<Unit> = runCatching {
        local.get(listId)?.let { list ->
            if (memberId !in list.memberIds) {
                local.upsert(list.copy(memberIds = list.memberIds + memberId))
            }
        }
    }
    override suspend fun addMembers(listId: String, members: List<ListMember>): Result<Unit> = runCatching {
        local.get(listId)?.let { list ->
            val updatedMembers = (list.members + members).distinctBy { it.uid }
            val updatedMemberIds = updatedMembers.map { it.uid }
            local.upsert(list.copy(members = updatedMembers, memberIds = updatedMemberIds, updatedAt = Clock.System.now()))
        }
    }
    override suspend fun removeMember(listId: String, memberId: String): Result<Unit> = runCatching {
        local.get(listId)?.let { list ->
            local.upsert(list.copy(memberIds = list.memberIds - memberId, members = list.members.filterNot { it.uid == memberId }))
        }
    }
}
