package com.snehonir.niyeesho.features.messaging.domain.usecase

import com.snehonir.niyeesho.features.messaging.domain.entity.InviteData
import com.snehonir.niyeesho.features.messaging.domain.repository.MessagingRepository

/** Accepts an invite by sending a CONTACT_ACCEPTED message to its owner. */
class SendContactRequestUseCase(private val repository: MessagingRepository) {
    suspend operator fun invoke(contact: InviteData): Result<Unit> =
        repository.sendContactRequest(contact)
}
