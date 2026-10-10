package com.snehonir.niyeesho.features.messaging.data.handler

import com.snehonir.niyeesho.core.utils.logD
import com.snehonir.niyeesho.features.messaging.domain.entity.FcmMessage
import com.snehonir.niyeesho.features.messaging.domain.entity.FcmMessageData
import com.snehonir.niyeesho.features.messaging.domain.entity.FcmMessageType
import com.snehonir.niyeesho.features.messaging.domain.entity.ShoppingListAction
import com.snehonir.niyeesho.features.shopping_list.data.local.ShoppingListLocalDataSource
import com.snehonir.niyeesho.features.shopping_list.domain.entity.ListMember
import com.snehonir.niyeesho.features.shopping_list.domain.entity.ShoppingList
import kotlinx.datetime.Clock

class ListMessageHandler(
    private val shoppingListLocal: ShoppingListLocalDataSource,
) : MessageHandler {
    override suspend fun handle(message: FcmMessage): Result<Unit> = runCatching {
        logD("ListMessageHandler", "handle() -> received type: ${message.type}, listId: ${message.listId}")

        when (val listData = message.data) {
            is FcmMessageData.ShoppingList -> {
                val listId = message.listId ?: listData.listId
                if (listId.isBlank()) return@runCatching

                val action = if (message.type == FcmMessageType.LIST_DELETED) ShoppingListAction.DELETE else listData.action

                when (action) {
                    ShoppingListAction.DELETE -> {
                        logD("ListMessageHandler", "action = DELETE -> removing listId: $listId from local DB")
                        shoppingListLocal.delete(listId)
                    }
                    ShoppingListAction.ADD, ShoppingListAction.UPDATE -> {
                        val title = listData.title.ifBlank { "Shared List" }
                        val now = Clock.System.now()
                        val existing = shoppingListLocal.get(listId)

                        val members = existing?.members ?: listOf(
                            ListMember(message.senderId, message.senderName, null)
                        )

                        val updatedList = ShoppingList(
                            id = listId,
                            title = title,
                            ownerId = existing?.ownerId ?: message.senderId,
                            memberIds = members.map { it.uid },
                            members = members,
                            createdAt = existing?.createdAt ?: now,
                            updatedAt = now,
                            lastSyncedAt = now,
                            isArchived = false,
                        )
                        logD("ListMessageHandler", "action = $action -> upserting list '$title' (id: $listId) into local DB")
                        shoppingListLocal.upsert(updatedList)
                    }
                }
            }
            else -> Unit
        }
    }
}
