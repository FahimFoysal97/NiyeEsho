package com.snehonir.niyeesho.features.messaging.data.handler

import com.snehonir.niyeesho.features.messaging.domain.entity.FcmMessage
import com.snehonir.niyeesho.features.messaging.domain.entity.FcmMessageType

class ItemMessageHandler(
    private val onMessage: suspend (FcmMessage) -> Result<Unit>,
) : MessageHandler {
    private val supported = setOf(
        FcmMessageType.ITEM_ADDED, FcmMessageType.ITEM_UPDATED, FcmMessageType.ITEM_TOGGLED,
        FcmMessageType.ITEM_DELETED, FcmMessageType.ITEMS_REORDERED,
    )

    override suspend fun handle(message: FcmMessage): Result<Unit> =
        if (message.type in supported) onMessage(message)
        else Result.failure(IllegalArgumentException("Unsupported item message."))
}
