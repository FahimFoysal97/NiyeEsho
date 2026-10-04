package com.snehonir.niyeesho.features.shopping_list.data.repository

import com.snehonir.niyeesho.core.utils.logD
import com.snehonir.niyeesho.features.drive.domain.entity.DriveListItem
import com.snehonir.niyeesho.features.drive.domain.entity.DriveListMember
import com.snehonir.niyeesho.features.drive.domain.entity.DriveShoppingList
import com.snehonir.niyeesho.features.drive.domain.repository.DriveRepository
import com.snehonir.niyeesho.features.messaging.domain.entity.FcmMessage
import com.snehonir.niyeesho.features.messaging.domain.entity.FcmMessageType
import com.snehonir.niyeesho.features.messaging.domain.repository.MessagingRepository
import com.snehonir.niyeesho.features.shopping_list.data.local.ListItemLocalDataSource
import com.snehonir.niyeesho.features.shopping_list.domain.entity.ListItem
import com.snehonir.niyeesho.features.shopping_list.domain.repository.ListItemRepository
import com.snehonir.niyeesho.features.shopping_list.domain.repository.ShoppingListRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.datetime.Clock

class ListItemRepositoryImpl(
    private val local: ListItemLocalDataSource,
    private val driveRepository: DriveRepository,
    private val messagingRepository: MessagingRepository,
    private val shoppingListRepository: ShoppingListRepository,
) : ListItemRepository {
    override fun watchListItems(listId: String): Flow<Result<List<ListItem>>> =
        local.watchForList(listId).map { Result.success(it) }.catch { emit(Result.failure(it)) }

    override suspend fun addItem(item: ListItem): Result<Unit> = runCatching {
        logD("ListItemRepo", "addItem() -> inserting item '${item.name}' (id: ${item.id}) for listId: ${item.listId} in LocalDB")
        local.upsert(item)
        logD("ListItemRepo", "addItem() -> successfully inserted item '${item.name}' in LocalDB")
        syncToRemote(item.listId)
        notifyMembers(item.listId, "addItem", item.name)
    }.onFailure {
        logD("ListItemRepo", "addItem() -> failed to insert item: ${it.message}")
    }

    override suspend fun updateItem(item: ListItem): Result<Unit> = runCatching {
        logD("ListItemRepo", "updateItem() -> updating item '${item.name}' id: ${item.id} in LocalDB")
        local.upsert(item.copy(updatedAt = Clock.System.now()))
        logD("ListItemRepo", "updateItem() -> success")
        syncToRemote(item.listId)
        notifyMembers(item.listId, "updateItem", item.name)
    }.onFailure {
        logD("ListItemRepo", "updateItem() -> failed: ${it.message}")
    }

    override suspend fun toggleItem(itemId: String, checked: Boolean, checkedBy: String?): Result<Unit> =
        runCatching {
            logD("ListItemRepo", "toggleItem() -> toggling itemId: $itemId to checked: $checked")
            local.get(itemId)?.let { item ->
                val updated = item.copy(isChecked = checked, checkedBy = checkedBy, updatedAt = Clock.System.now())
                local.upsert(updated)
                logD("ListItemRepo", "toggleItem() -> success in LocalDB")
                syncToRemote(item.listId)
                notifyMembers(item.listId, "toggleItem", item.name)
            } ?: logD("ListItemRepo", "toggleItem() -> item not found: $itemId")
        }.onFailure {
            logD("ListItemRepo", "toggleItem() -> failed: ${it.message}")
        }

    override suspend fun deleteItem(itemId: String): Result<Unit> = runCatching {
        logD("ListItemRepo", "deleteItem() -> deleting itemId: $itemId from LocalDB")
        val item = local.get(itemId)
        local.delete(itemId)
        logD("ListItemRepo", "deleteItem() -> success")
        item?.let {
            syncToRemote(it.listId)
            notifyMembers(it.listId, "deleteItem", it.name)
        }
        Unit
    }.onFailure {
        logD("ListItemRepo", "deleteItem() -> failed: ${it.message}")
    }

    override suspend fun reorderItems(listId: String, orderedIds: List<String>): Result<Unit> =
        runCatching {
            logD("ListItemRepo", "reorderItems() for listId: $listId")
            local.reorder(listId, orderedIds)
            logD("ListItemRepo", "reorderItems() -> success")
            syncToRemote(listId)
        }.onFailure {
            logD("ListItemRepo", "reorderItems() -> failed: ${it.message}")
        }

    private suspend fun syncToRemote(listId: String) {
        runCatching {
            logD("ListItemRepo", "syncToRemote() -> backing up listId: $listId to Google Drive")
            val listResult = shoppingListRepository.getShoppingList(listId)
            if (listResult.isSuccess) {
                val list = listResult.getOrThrow()
                val items = local.watchForList(listId).first()
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
                        DriveListItem(
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
                driveRepository.saveList("demo_access_token", driveList)
                logD("ListItemRepo", "syncToRemote() -> Google Drive backup success for listId: $listId")
            }
        }.onFailure {
            logD("ListItemRepo", "syncToRemote() -> Google Drive backup failed: ${it.message}")
        }
    }

    private suspend fun notifyMembers(listId: String, actionName: String, itemName: String) {
        runCatching {
            val listResult = shoppingListRepository.getShoppingList(listId)
            if (listResult.isSuccess) {
                val list = listResult.getOrThrow()
                val otherMembers = list.memberIds.filter { it != "user_demo" }
                otherMembers.forEach { memberUid ->
                    logD("ListItemRepo", "notifyMembers() -> broadcasting FCM message ($actionName) to member: $memberUid")
                    messagingRepository.send(
                        recipientUid = memberUid,
                        message = FcmMessage(
                            type = FcmMessageType.ITEM_ADDED,
                            senderId = "user_demo",
                            senderName = "Sneho Nir",
                            listId = listId,
                            timestamp = Clock.System.now(),
                            data = "{\"action\":\"$actionName\",\"item\":\"$itemName\"}",
                        ),
                    )
                }
            }
        }.onFailure {
            logD("ListItemRepo", "notifyMembers() -> failed: ${it.message}")
        }
    }
}
