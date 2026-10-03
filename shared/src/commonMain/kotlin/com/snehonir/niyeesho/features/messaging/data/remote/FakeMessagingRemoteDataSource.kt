package com.snehonir.niyeesho.features.messaging.data.remote

import com.snehonir.niyeesho.features.messaging.data.remote.dto.FcmPayloadDto
import kotlinx.coroutines.delay

class FakeMessagingRemoteDataSource : MessagingRemoteDataSource {
    override suspend fun send(recipientToken: String, payload: FcmPayloadDto): Result<Unit> {
        delay(60)
        return if (recipientToken.isBlank()) {
            Result.failure(IllegalArgumentException("Recipient FCM token is blank."))
        } else {
            Result.success(Unit)
        }
    }
}
