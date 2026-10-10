package com.snehonir.niyeesho.features.messaging.domain.entity

import kotlinx.datetime.Instant

data class FcmMessage(
    val type: FcmMessageType,
    val senderId: String,
    val senderName: String,
    val listId: String?,
    val timestamp: Instant,
    val data: FcmMessageData = FcmMessageData.Generic(),
) {
    companion object {
        fun preview() = FcmMessage(
            type = FcmMessageType.ITEM_ADDED,
            senderId = "user_demo",
            senderName = "Sneho Nir",
            listId = "list_001",
            timestamp = Instant.parse("2026-06-15T10:00:00Z"),
            data = FcmMessageData.ShoppingListItem(action = ShoppingListItemAction.ADD, itemId = "item_001", name = "Milk"),
        )
    }
}
