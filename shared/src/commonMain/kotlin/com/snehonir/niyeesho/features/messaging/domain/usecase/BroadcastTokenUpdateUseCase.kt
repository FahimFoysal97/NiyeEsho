package com.snehonir.niyeesho.features.messaging.domain.usecase

import com.snehonir.niyeesho.features.messaging.domain.entity.FcmMessage
import com.snehonir.niyeesho.features.messaging.domain.entity.FcmMessageType
import com.snehonir.niyeesho.features.messaging.domain.repository.MessagingRepository
import kotlinx.datetime.Clock

/** Broadcasts the current device token to all connected contacts. */
class BroadcastTokenUpdateUseCase(private val repository: MessagingRepository) {
    suspend operator fun invoke(
        senderId: String,
        senderName: String,
        token: String,
        recipients: List<String>,
    ): Result<Unit> {
        val message = FcmMessage(
            type = FcmMessageType.TOKEN_UPDATE,
            senderId = senderId,
            senderName = senderName,
            listId = null,
            timestamp = Clock.System.now(),
            data = "{\"token\":\"$token\"}",
        )
        recipients.forEach { recipient ->
            repository.send(recipient, message).getOrElse { return Result.failure(it) }
        }
        return Result.success(Unit)
    }
}
