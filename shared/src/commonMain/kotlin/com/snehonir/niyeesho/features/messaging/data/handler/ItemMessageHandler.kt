package com.snehonir.niyeesho.features.messaging.data.handler

import com.snehonir.niyeesho.core.utils.logD
import com.snehonir.niyeesho.features.messaging.domain.entity.FcmMessage
import com.snehonir.niyeesho.features.messaging.domain.entity.FcmMessageData
import com.snehonir.niyeesho.features.messaging.domain.entity.FcmMessageType
import com.snehonir.niyeesho.features.messaging.domain.entity.ShoppingListItemAction
import com.snehonir.niyeesho.features.shopping_list.data.local.ListItemLocalDataSource
import com.snehonir.niyeesho.features.shopping_list.domain.entity.ListItem
import kotlinx.datetime.Clock

class ItemMessageHandler(
    private val listItemLocal: ListItemLocalDataSource,
) : MessageHandler {
    override suspend fun handle(message: FcmMessage): Result<Unit> = runCatching {
        logD("ItemMessageHandler", "handle() -> received type: ${message.type}, listId: ${message.listId}")

        when (val itemData = message.data) {
            is FcmMessageData.ShoppingListItem -> {
                val listId = message.listId ?: itemData.listId
                val action = if (message.type == FcmMessageType.ITEM_DELETED) ShoppingListItemAction.DELETE else itemData.action

                when (action) {
                    ShoppingListItemAction.DELETE -> {
                        if (itemData.itemId.isNotBlank()) {
                            logD("ItemMessageHandler", "action = DELETE -> removing itemId: ${itemData.itemId} from local DB")
                            listItemLocal.delete(itemData.itemId)
                        }
                    }
                    ShoppingListItemAction.ADD, ShoppingListItemAction.UPDATE -> {
                        if (itemData.itemId.isNotBlank() && listId.isNotBlank()) {
                            val now = Clock.System.now()
                            val item = ListItem(
                                id = itemData.itemId,
                                listId = listId,
                                name = itemData.name.ifBlank { "Item" },
                                quantity = itemData.quantity ?: "1",
                                unit = itemData.unit,
                                isChecked = itemData.isChecked,
                                addedBy = message.senderId,
                                addedByName = message.senderName,
                                checkedBy = if (itemData.isChecked) message.senderId else null,
                                position = 0,
                                createdAt = now,
                                updatedAt = now,
                            )
                            logD("ItemMessageHandler", "action = $action -> upserting item '${item.name}' (id: ${itemData.itemId}) for list: $listId")
                            listItemLocal.upsert(item)
                        }
                    }
                    ShoppingListItemAction.REORDER -> Unit
                }
            }
            is FcmMessageData.Generic -> {
                if (message.type == FcmMessageType.ITEM_DELETED && itemData.text.isNotBlank()) {
                    logD("ItemMessageHandler", "ITEM_DELETED generic text data: ${itemData.text}")
                    listItemLocal.delete(itemData.text)
                }
            }
            else -> Unit
        }
    }
}
