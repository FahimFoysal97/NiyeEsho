package com.snehonir.niyeesho.features.messaging.domain.usecase

import com.snehonir.niyeesho.features.messaging.domain.entity.InviteData
import com.snehonir.niyeesho.features.messaging.domain.repository.MessagingRepository

/** Builds a shareable NiyeEsho invite deep link. */
class GenerateInviteLinkUseCase(private val repository: MessagingRepository) {
    suspend operator fun invoke(data: InviteData): Result<String> =
        repository.generateInviteLink(data)
}
