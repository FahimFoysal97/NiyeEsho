package com.snehonir.niyeesho.features.messaging.domain.usecase

import com.snehonir.niyeesho.features.messaging.domain.entity.FcmMessage
import com.snehonir.niyeesho.features.messaging.domain.repository.MessagingRepository

/** Applies an incoming FCM message to the local-first data flow. */
class HandleIncomingMessageUseCase(private val repository: MessagingRepository) {
    suspend operator fun invoke(message: FcmMessage): Result<Unit> =
        repository.handleIncoming(message)
}
