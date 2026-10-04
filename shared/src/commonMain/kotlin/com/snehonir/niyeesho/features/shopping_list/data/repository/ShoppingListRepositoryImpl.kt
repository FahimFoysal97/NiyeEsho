package com.snehonir.niyeesho.features.shopping_list.data.repository

import com.snehonir.niyeesho.core.utils.TokenManager
import com.snehonir.niyeesho.core.utils.logD
import com.snehonir.niyeesho.features.drive.domain.entity.DriveListMember
import com.snehonir.niyeesho.features.drive.domain.entity.DriveShoppingList
import com.snehonir.niyeesho.features.drive.domain.repository.DriveRepository
import com.snehonir.niyeesho.features.shopping_list.data.local.ListItemLocalDataSource
import com.snehonir.niyeesho.features.shopping_list.data.local.ShoppingListLocalDataSource
import com.snehonir.niyeesho.features.shopping_list.domain.entity.ListMember
import com.snehonir.niyeesho.features.shopping_list.domain.entity.ShoppingList
import com.snehonir.niyeesho.features.shopping_list.domain.repository.ShoppingListRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.datetime.Clock

class ShoppingListRepositoryImpl(
    private val local: ShoppingListLocalDataSource,
    private val listItemLocal: ListItemLocalDataSource,
    private val driveRepository: DriveRepository,
    private val tokenManager: TokenManager,
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
        logD("ShoppingListRepo", "createShoppingList() -> created list '$title' ($listId)")
        syncListToRemote(list)
        list
    }

    override suspend fun updateShoppingList(list: ShoppingList): Result<Unit> = runCatching {
        val updated = list.copy(updatedAt = Clock.System.now())
        local.upsert(updated)
        logD("ShoppingListRepo", "updateShoppingList() -> updated list '${list.title}' (${list.id})")
        syncListToRemote(updated)
    }

    override suspend fun deleteShoppingList(id: String): Result<Unit> = runCatching {
        local.delete(id)
        logD("ShoppingListRepo", "deleteShoppingList() -> deleted list id: $id")
    }

    override suspend fun archiveShoppingList(id: String): Result<Unit> = runCatching {
        val list = local.get(id) ?: return@runCatching
        val archived = list.copy(isArchived = true, updatedAt = Clock.System.now())
        local.upsert(archived)
        logD("ShoppingListRepo", "archiveShoppingList() -> archived list id: $id")
        syncListToRemote(archived)
    }

    override suspend fun inviteMember(listId: String, memberId: String): Result<Unit> = runCatching {
        val list = local.get(listId) ?: return@runCatching
        if (memberId !in list.memberIds) {
            val updated = list.copy(memberIds = list.memberIds + memberId, updatedAt = Clock.System.now())
            local.upsert(updated)
            logD("ShoppingListRepo", "inviteMember() -> invited member $memberId to list $listId")
            syncListToRemote(updated)
        }
    }

    override suspend fun addMembers(listId: String, members: List<ListMember>): Result<Unit> = runCatching {
        val list = local.get(listId) ?: return@runCatching
        val updatedMembers = (list.members + members).distinctBy { it.uid }
        val updatedMemberIds = updatedMembers.map { it.uid }
        val updated = list.copy(members = updatedMembers, memberIds = updatedMemberIds, updatedAt = Clock.System.now())
        local.upsert(updated)
        logD("ShoppingListRepo", "addMembers() -> added members to list $listId")
        syncListToRemote(updated)
    }

    override suspend fun removeMember(listId: String, memberId: String): Result<Unit> = runCatching {
        val list = local.get(listId) ?: return@runCatching
        val updated = list.copy(memberIds = list.memberIds - memberId, members = list.members.filterNot { it.uid == memberId }, updatedAt = Clock.System.now())
        local.upsert(updated)
        logD("ShoppingListRepo", "removeMember() -> removed member $memberId from list $listId")
        syncListToRemote(updated)
    }

    private suspend fun syncListToRemote(list: ShoppingList) {
        runCatching {
            val token = tokenManager.getToken()
            if (token.isNullOrBlank()) {
                logD("ShoppingListRepo", "syncListToRemote() -> skipped: No access token found.")
                return
            }
            logD("ShoppingListRepo", "syncListToRemote() -> backing up list '${list.title}' (${list.id}) to Google Drive")
            val items = listItemLocal.watchForList(list.id).first()
            val driveList = DriveShoppingList(
                id = list.id,
                title = list.title,
                ownerId = list.ownerId,
                memberIds = list.memberIds,
                members = list.members.map { DriveListMember(it.uid, it.displayName, it.avatarUrl) },
                createdAt = list.createdAt,
                updatedAt = list.updatedAt,
                lastSyncedAt = list.lastSyncedAt,
                items = items.map { item ->
                    com.snehonir.niyeesho.features.drive.domain.entity.DriveListItem(
                        id = item.id,
                        name = item.name,
                        quantity = item.quantity,
                        unit = item.unit,
                        isChecked = item.isChecked,
                        addedBy = item.addedBy,
                        addedByName = item.addedByName,
                        checkedBy = item.checkedBy,
                        position = item.position,
                        createdAt = item.createdAt,
                        updatedAt = item.updatedAt,
                    )
                },
                isArchived = list.isArchived,
            )
            driveRepository.saveList(token, driveList)
            logD("ShoppingListRepo", "syncListToRemote() -> Google Drive backup success for list '${list.title}'")
        }.onFailure {
            logD("ShoppingListRepo", "syncListToRemote() -> Google Drive backup failed: ${it.message}")
        }
    }
}
