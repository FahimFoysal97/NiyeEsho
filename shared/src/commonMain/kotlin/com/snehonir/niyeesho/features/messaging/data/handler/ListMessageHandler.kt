package com.snehonir.niyeesho.features.messaging.data.handler

import com.snehonir.niyeesho.features.messaging.domain.entity.FcmMessage
import com.snehonir.niyeesho.features.messaging.domain.entity.FcmMessageType

class ListMessageHandler(
    private val onMessage: suspend (FcmMessage) -> Result<Unit>,
) : MessageHandler {
    private val supported = setOf(
        FcmMessageType.LIST_SHARED, FcmMessageType.LIST_UPDATED, FcmMessageType.LIST_DELETED,
        FcmMessageType.LIST_ARCHIVED, FcmMessageType.MEMBER_ADDED, FcmMessageType.MEMBER_REMOVED,
    )

    override suspend fun handle(message: FcmMessage): Result<Unit> =
        if (message.type in supported) onMessage(message)
        else Result.failure(IllegalArgumentException("Unsupported list message."))
}
