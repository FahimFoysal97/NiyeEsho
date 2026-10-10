package com.snehonir.niyeesho.features.messaging.domain.usecase

import com.snehonir.niyeesho.features.messaging.domain.entity.InviteData
import com.snehonir.niyeesho.features.messaging.domain.repository.MessagingRepository

/** Sends a connection request to an invite creator using their FCM token. */
class SendContactRequestUseCase(private val repository: MessagingRepository) {
    suspend operator fun invoke(recipientUid: String, recipientToken: String, senderData: InviteData): Result<Unit> =
        repository.sendContactRequest(recipientUid, recipientToken, senderData)
}
