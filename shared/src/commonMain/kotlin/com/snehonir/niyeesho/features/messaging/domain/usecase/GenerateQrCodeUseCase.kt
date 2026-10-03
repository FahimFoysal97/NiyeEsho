package com.snehonir.niyeesho.features.messaging.domain.usecase

import com.snehonir.niyeesho.features.messaging.domain.entity.InviteData
import com.snehonir.niyeesho.features.messaging.domain.repository.MessagingRepository

/** Returns the invite payload used by a platform QR renderer. */
class GenerateQrCodeUseCase(private val repository: MessagingRepository) {
    suspend operator fun invoke(data: InviteData): Result<String> =
        repository.generateInviteLink(data)
}
