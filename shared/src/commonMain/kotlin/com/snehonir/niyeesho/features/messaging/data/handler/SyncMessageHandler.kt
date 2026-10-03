package com.snehonir.niyeesho.features.messaging.data.handler

import com.snehonir.niyeesho.features.messaging.domain.entity.FcmMessage
import com.snehonir.niyeesho.features.messaging.domain.entity.FcmMessageType

class SyncMessageHandler(
    private val onMessage: suspend (FcmMessage) -> Result<Unit>,
) : MessageHandler {
    override suspend fun handle(message: FcmMessage): Result<Unit> =
        if (message.type == FcmMessageType.SYNC_REQUEST ||
            message.type == FcmMessageType.SYNC_RESPONSE
        ) onMessage(message) else Result.failure(IllegalArgumentException("Unsupported sync message."))
}
