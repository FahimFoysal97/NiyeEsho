package com.snehonir.niyeesho.features.messaging.data.handler

import com.snehonir.niyeesho.features.messaging.domain.entity.FcmMessage
import com.snehonir.niyeesho.features.messaging.domain.entity.FcmMessageType

class ContactMessageHandler(
    private val onMessage: suspend (FcmMessage) -> Result<Unit>,
) : MessageHandler {
    override suspend fun handle(message: FcmMessage): Result<Unit> =
        if (message.type == FcmMessageType.CONTACT_ACCEPTED ||
            message.type == FcmMessageType.CONTACT_REMOVED
        ) onMessage(message) else Result.failure(IllegalArgumentException("Unsupported contact message."))
}
