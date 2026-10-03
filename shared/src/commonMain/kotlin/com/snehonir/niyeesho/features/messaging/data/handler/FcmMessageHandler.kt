package com.snehonir.niyeesho.features.messaging.data.handler

import com.snehonir.niyeesho.features.messaging.domain.entity.FcmMessage
import com.snehonir.niyeesho.features.messaging.domain.entity.FcmMessageType

interface MessageHandler {
    suspend fun handle(message: FcmMessage): Result<Unit>
}

class FcmMessageHandler(
    private val contactHandler: ContactMessageHandler,
    private val listHandler: ListMessageHandler,
    private val itemHandler: ItemMessageHandler,
    private val tokenHandler: TokenMessageHandler,
    private val syncHandler: SyncMessageHandler,
) : MessageHandler {
    override suspend fun handle(message: FcmMessage): Result<Unit> = when (message.type) {
        FcmMessageType.CONTACT_ACCEPTED, FcmMessageType.CONTACT_REMOVED ->
            contactHandler.handle(message)
        FcmMessageType.LIST_SHARED, FcmMessageType.LIST_UPDATED, FcmMessageType.LIST_DELETED,
        FcmMessageType.LIST_ARCHIVED, FcmMessageType.MEMBER_ADDED, FcmMessageType.MEMBER_REMOVED ->
            listHandler.handle(message)
        FcmMessageType.ITEM_ADDED, FcmMessageType.ITEM_UPDATED, FcmMessageType.ITEM_TOGGLED,
        FcmMessageType.ITEM_DELETED, FcmMessageType.ITEMS_REORDERED ->
            itemHandler.handle(message)
        FcmMessageType.TOKEN_UPDATE -> tokenHandler.handle(message)
        FcmMessageType.SYNC_REQUEST, FcmMessageType.SYNC_RESPONSE -> syncHandler.handle(message)
    }
}
