package com.snehonir.niyeesho.features.messaging.data.handler

import com.snehonir.niyeesho.features.messaging.domain.entity.FcmMessage
import com.snehonir.niyeesho.features.messaging.domain.entity.FcmMessageType

class TokenMessageHandler(
    private val onMessage: suspend (FcmMessage) -> Result<Unit>,
) : MessageHandler {
    override suspend fun handle(message: FcmMessage): Result<Unit> =
        if (message.type == FcmMessageType.TOKEN_UPDATE) onMessage(message)
        else Result.failure(IllegalArgumentException("Unsupported token message."))
}
