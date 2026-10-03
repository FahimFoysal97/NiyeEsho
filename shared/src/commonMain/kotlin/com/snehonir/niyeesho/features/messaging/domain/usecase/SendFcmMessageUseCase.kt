package com.snehonir.niyeesho.features.messaging.domain.usecase

import com.snehonir.niyeesho.features.messaging.domain.entity.FcmMessage
import com.snehonir.niyeesho.features.messaging.domain.repository.MessagingRepository

/** Sends a data-only FCM message or queues it for retry when offline. */
class SendFcmMessageUseCase(private val repository: MessagingRepository) {
    suspend operator fun invoke(recipientUid: String, message: FcmMessage): Result<Unit> =
        repository.send(recipientUid, message)
}
