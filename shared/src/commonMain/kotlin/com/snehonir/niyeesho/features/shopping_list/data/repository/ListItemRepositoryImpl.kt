package com.snehonir.niyeesho.features.shopping_list.data.repository

import com.snehonir.niyeesho.core.utils.TokenManager
import com.snehonir.niyeesho.core.utils.logD
import com.snehonir.niyeesho.features.drive.domain.entity.DriveListItem
import com.snehonir.niyeesho.features.drive.domain.entity.DriveListMember
import com.snehonir.niyeesho.features.drive.domain.entity.DriveShoppingList
import com.snehonir.niyeesho.features.drive.domain.repository.DriveRepository
import com.snehonir.niyeesho.features.messaging.domain.entity.FcmMessage
import com.snehonir.niyeesho.features.messaging.domain.entity.FcmMessageData
import com.snehonir.niyeesho.features.messaging.domain.entity.FcmMessageType
import com.snehonir.niyeesho.features.messaging.domain.entity.ShoppingListItemAction
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
    private val tokenManager: TokenManager,
) : ListItemRepository {
    override fun watchListItems(listId: String): Flow<Result<List<ListItem>>> =
        local.watchForList(listId).map { Result.success(it) }.catch { emit(Result.failure(it)) }

    override suspend fun addItem(item: ListItem): Result<Unit> = runCatching {
        logD("ListItemRepo", "addItem() -> inserting item '${item.name}' (id: ${item.id}) for listId: ${item.listId} in LocalDB")
        local.upsert(item)
        touchParentList(item.listId)
        logD("ListItemRepo", "addItem() -> successfully inserted item '${item.name}' in LocalDB")
        syncToRemote(item.listId)
        notifyMembers(item.listId, FcmMessageType.ITEM_ADDED, item.name)
    }.onFailure {
        logD("ListItemRepo", "addItem() -> failed to insert item: ${it.message}")
    }

    override suspend fun updateItem(item: ListItem): Result<Unit> = runCatching {
        logD("ListItemRepo", "updateItem() -> updating item '${item.name}' id: ${item.id} in LocalDB")
        local.upsert(item.copy(updatedAt = Clock.System.now()))
        touchParentList(item.listId)
        logD("ListItemRepo", "updateItem() -> success")
        syncToRemote(item.listId)
        notifyMembers(item.listId, FcmMessageType.ITEM_UPDATED, item.name)
    }.onFailure {
        logD("ListItemRepo", "updateItem() -> failed: ${it.message}")
    }

    override suspend fun toggleItem(itemId: String, checked: Boolean, checkedBy: String?): Result<Unit> =
        runCatching {
            logD("ListItemRepo", "toggleItem() -> toggling itemId: $itemId to checked: $checked")
            local.get(itemId)?.let { item ->
                val updated = item.copy(isChecked = checked, checkedBy = checkedBy, updatedAt = Clock.System.now())
                local.upsert(updated)
                touchParentList(item.listId)
                logD("ListItemRepo", "toggleItem() -> success in LocalDB")
                syncToRemote(item.listId)
                notifyMembers(item.listId, FcmMessageType.ITEM_TOGGLED, item.name)
            } ?: logD("ListItemRepo", "toggleItem() -> item not found: $itemId")
        }.onFailure {
            logD("ListItemRepo", "toggleItem() -> failed: ${it.message}")
        }

    override suspend fun deleteItem(itemId: String): Result<Unit> = runCatching {
        logD("ListItemRepo", "deleteItem() -> deleting itemId: $itemId from LocalDB")
        val item = local.get(itemId)
        local.delete(itemId)
        item?.let {
            touchParentList(it.listId)
            syncToRemote(it.listId)
            notifyMembers(it.listId, FcmMessageType.ITEM_DELETED, it.name)
        }
        logD("ListItemRepo", "deleteItem() -> success")
    }.onFailure {
        logD("ListItemRepo", "deleteItem() -> failed: ${it.message}")
    }

    override suspend fun reorderItems(listId: String, orderedIds: List<String>): Result<Unit> = runCatching {
        local.reorder(listId, orderedIds)
        touchParentList(listId)
        syncToRemote(listId)
    }.onFailure {
        logD("ListItemRepo", "reorderItems() -> failed: ${it.message}")
    }

    private suspend fun touchParentList(listId: String) {
        runCatching {
            shoppingListRepository.getShoppingList(listId).onSuccess { list ->
                shoppingListRepository.updateShoppingList(list)
            }
        }
    }

    private suspend fun syncToRemote(listId: String) {
        runCatching {
            val token = tokenManager.getToken()
            if (token.isNullOrBlank()) return
            val list = shoppingListRepository.getShoppingList(listId).getOrNull() ?: return
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
            driveRepository.saveList(token, driveList)
        }.onFailure {
            logD("ListItemRepo", "syncToRemote() -> failed: ${it.message}")
        }
    }

    private suspend fun notifyMembers(listId: String, type: FcmMessageType, itemName: String) {
        runCatching {
            val list = shoppingListRepository.getShoppingList(listId).getOrNull() ?: return
            val message = FcmMessage(
                type = type,
                senderId = "user_demo",
                senderName = "Sneho Nir",
                listId = listId,
                timestamp = Clock.System.now(),
                data = FcmMessageData.ShoppingListItem(action = ShoppingListItemAction.UPDATE, listId = listId, name = itemName),
            )
            for (memberId in list.memberIds) {
                if (memberId != "user_demo") {
                    messagingRepository.send(recipientUid = memberId, recipientToken = "", message = message)
                }
            }
        }.onFailure {
            logD("ListItemRepo", "notifyMembers() -> failed: ${it.message}")
        }
    }
}
